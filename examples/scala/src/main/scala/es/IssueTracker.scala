package es

import java.util.UUID

// types
case class IssueId(value: String)
case class UserId(value: String)
case class MilestoneId(value: String)

// events
sealed trait Event
case class Created(issueId: IssueId, title: String) extends Event
case class Commented(issueId: IssueId, comment: String) extends Event
case class Assigned(issueId: IssueId, userId: UserId) extends Event
case class Planned(issueId: IssueId, milestoneId: MilestoneId) extends Event
case class Categorised(issueId: IssueId, label: String) extends Event


sealed trait State { def id: IssueId }
case class Empty(id: IssueId) extends State
case class Existing(id: IssueId) extends State

// behaviors
trait IssueTracker {

  def handle(command: Submit)(state: Empty): (List[Event], Existing) = {
    // utility functions
    val appendCategorised = (es: List[Event]) => {
      val categorisedEvent = command.categories map((c: String) => Categorised(state.id, c))
      es ++ categorisedEvent
    }

    val appendAssigned = (es: List[Event]) => {
      val assignedEvent = command.assignees map((u: UserId) => Assigned(state.id, u))
      es ++ assignedEvent
    }

    val appendPlanned = (es: List[Event]) => {
      val plannedEvents = command.milestones map((m: MilestoneId) => Planned(state.id, m))
      es ++ plannedEvents
    }

    val appendCommented: List[Event] => List[Event] = (es: List[Event]) => {
      command.commentO match {
        case Some(comment) => es :+ Commented(state.id, comment)
        case _             => es
      }
    }

    val appendCreated = (es: List[Event]) => {
      es :+ Created(state.id, command.title)
    }

    val composition = appendCreated andThen appendCommented andThen appendPlanned andThen appendAssigned andThen appendCategorised

    composition(List()) -> Existing(state.id)
  }

  def handle(command: AddComment)(state: Existing): (List[Event], State) = {
    List(
      Commented(state.id, command.comment)
    ) -> state
  }

}

// commands
sealed trait Command
case class Submit(title: String, authorId: UserId commentO: Option[String], assignees: List[UserId], milestones: List[MilestoneId], categories: List[String]) extends Command
case class AddComment(comment: String) extends Command
