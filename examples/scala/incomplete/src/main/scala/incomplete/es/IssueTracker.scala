package incomplete.es

import java.util.UUID

// types
case class IssueId(value: String)
case class UserId(value: String)
case class MilestoneId(value: String)

// events
// events
sealed trait Event {
  def issueId: IssueId
}

case class Created(issueId: IssueId, authorId: UserId, title: String) extends Event

case class Commented(issueId: IssueId, comment: String) extends Event

case class Assigned(issueId: IssueId, userId: UserId) extends Event

case class Planned(issueId: IssueId, milestoneId: MilestoneId) extends Event

case class Categorised(issueId: IssueId, label: String) extends Event

// log
trait Log {
  // Append the event to the log
  def append(events: List[Event]): Unit

  // Fetch the events related to the given issue from the log
  def fetch(id: IssueId): List[Event]
}

trait IdentitySupplier {
  def nextId: IssueId
}

/**
  * User cases implementation
  */
trait IssueTracker { this: Log with IdentitySupplier with Projection[Issue] =>

  def handle(command: Submit): (List[Event], Issue) = {

    val id = nextId

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

    val composition = appendCreated andThen
                      appendCommented andThen
                      appendPlanned andThen
                      appendAssigned andThen
                      appendCategorised

    val events = composition(List())
    append(events)

    events -> apply(initState)(events)
  }

  def handle(command: AddComment): (List[Event], Issue) = {
    val events = fetch(command.issueId)
    if (events.size > 0) {
      // if events exists for this issueId => append new Commented event
      val newEvents = List(
        Commented(command.issueId, command.comment)
      )
      // append new events to the log
      append(newEvents)

      val oldState: Issue = apply(initState)(events)
      val newState: Issue = apply(oldState)(newEvents)
      // return events
      newEvents -> newState
    } else {
      // else return empty list
      List() -> Empty
    }

  }

}

// commands
sealed trait Command
case class Submit(title: String, authorId: UserId, commentO: Option[String], assignees: List[UserId], milestones: List[MilestoneId], categories: List[String]) extends Command
case class AddComment(issueId: IssueId, comment: String) extends Command


trait InMemoryLog extends Log {

  private var records: List[Event] = List()

  def append(events: List[Event]): Unit = {
    records = records ++ events
  }

  def fetch(issueId: IssueId): List[Event] = {
    records filter ((e: Event) => e.issueId == issueId)
  }

}

trait UUIDNextId extends IdentitySupplier {
  def nextId = IssueId(UUID.randomUUID().toString)
}

// Projection
trait Projection[S] {
  def apply(state: S)(events: List[Event]): S
  def initState: S
}

trait IssueStatus
object Open extends IssueStatus
object Closed extends IssueStatus

sealed trait Issue
object Empty extends Issue
case class Existing(
  id: IssueId,
  title: String,
  author: UserId,
  status: IssueStatus,
  comments: List[String],
  assigneees: List[UserId],
  milestones: List[MilestoneId],
  labels: List[String]
) extends Issue

trait IssueProjection extends Projection[Issue] {
  override def apply(state: Issue)(events: List[Event]): Issue = {
    events.foldLeft(state)(applySingle)
  }

  override def initState: Issue = Empty

  private def applySingle(state: Issue, event: Event): Issue = state match {
    case Empty => event match {
      case Created(issueId, authorId, title) => Existing(
        issueId,
        title,
        authorId,
        Open,
        List(),
        List(),
        List(),
        List()
      )
      case _ => throw new IllegalStateException
    }
    case (s: Existing) => event match {
      case Commented(_, comment) => s.copy(comments = s.comments :+ comment)
      case Assigned(_, assigneeId) => s.copy(assigneees = s.assigneees :+ assigneeId)
      case Planned(_, milestoneId) => s.copy(milestones = s.milestones :+ milestoneId)
      case Categorised(_, label) => s.copy(labels = s.labels :+ label)
      case _ => throw new IllegalStateException
    }
  }
}


object IssueTrackerImpl extends IssueTracker with InMemoryLog with UUIDNextId with IssueProjection { }
