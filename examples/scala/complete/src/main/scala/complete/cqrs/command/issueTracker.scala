package complete.cqrs.command

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
class IssueTracker(log: Log, identitySupplier: IdentitySupplier) {

  private val projection: writeModelProjection = new writeModelProjection

  def handle(command: Submit): (List[Event], WriteModel) = {

    val id = identitySupplier.nextId

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

    val events = composition(List())
    log.append(events)

    events -> projection.apply(projection.initState)(events)
  }

  def handle(command: AddComment): (List[Event], WriteModel) = {
    val events = log.fetch(command.issueId)
    val currentModel = projection.apply(Empty)(events)

    val newEvents = currentModel match {
      case Open(_) => List(Commented(command.issueId, command.comment))
      case _ => List()
    }

    log.append(newEvents)

    val newState = projection.apply(currentModel)(newEvents)
    newEvents -> newState
  }

  def handle(command: Close): (List[Event], WriteModel) = {
    val events = log.fetch(command.issueId)
    val currentModel = projection.apply(Empty)(events)

    val newEvents = currentModel match {
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

    log.append(newEvents)

    val newState = projection.apply(currentModel)(newEvents)
    newEvents -> newState
  }

}

class InMemoryLog extends Log {

  private var records: List[Event] = List()

  override def append(events: List[Event]): Unit = {
    records = records ++ events
  }

  override def fetch(issueId: IssueId): List[Event] = {
    records filter ((e: Event) => e.issueId == issueId)
  }

  override def all: List[Event] = records
}

class UUIDNextId extends IdentitySupplier {
  def nextId = IssueId(UUID.randomUUID().toString)
}