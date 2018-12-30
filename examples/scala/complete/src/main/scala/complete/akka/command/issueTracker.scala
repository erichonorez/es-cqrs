package complete.akka.command

import complete.cqrs._
import java.util.UUID



// commands
sealed trait Command
case class Submit(title: String, authorId: UserId, commentO: Option[String], assignees: List[UserId], milestones: List[MilestoneId], categories: List[String]) extends Command
case class AddComment(issueId: IssueId, comment: String) extends Command
case class Close(issueId: IssueId, commentO: Option[String]) extends Command

/**
  * User cases implementation
  */
object IssueTracker {

  def handle(command: Submit)(state: WriteModel): List[Event] = {
    state match {
      case Empty(id)  => {

        // utility functions
        val appendCategorised = (es: List[Event]) => {
          val categorisedEvent = command.categories map((c: String) => Categorised(id, c))
          es ++ categorisedEvent
        }

        val appendAssigned = (es: List[Event]) => {
          val assignedEvent = command.assignees map((u: UserId) => Assigned(id, u))
          es ++ assignedEvent
        }

        val appendPlanned = (es: List[Event]) => {
          val plannedEvents = command.milestones map((m: MilestoneId) => Planned(id, m))
          es ++ plannedEvents
        }

        val appendCommented: List[Event] => List[Event] = (es: List[Event]) => {
          command.commentO match {
            case Some(comment) => es :+ Commented(id, comment)
            case _             => es
          }
        }

        val appendCreated = (es: List[Event]) => {
          es :+ Created(id, command.authorId, command.title)
        }

        val composition = appendCreated andThen appendCommented andThen appendPlanned andThen appendAssigned andThen appendCategorised

        composition(List())
      }
      case _          => List()

    }
  }

  def handle(command: AddComment)(state: WriteModel): List[Event] = state match {
    case Open(_) => List(Commented(command.issueId, command.comment))
    case _ => List()
  }

  def handle(command: Close)(state: WriteModel): List[Event] = state match {
    case Open(_) => {
      val optionalEvents = if (command.commentO.isDefined) {
        List(Commented(command.issueId, command.commentO.get))
      } else {
        List()
      }

      optionalEvents :+ Closed(command.issueId)
    }
    case _ => List()
  }

}