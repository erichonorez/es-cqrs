package complete.simplecqrs

import java.util.UUID

import complete.share.{IssueId, MilestoneId, UserId}

// events
sealed trait Event {
  def issueId: IssueId
}

case class Created(issueId: IssueId, authorId: UserId, title: String) extends Event
case class Commented(issueId: IssueId, comment: String) extends Event
case class Assigned(issueId: IssueId, userId: UserId) extends Event
case class Planned(issueId: IssueId, milestoneId: MilestoneId) extends Event
case class Categorised(issueId: IssueId, label: String) extends Event
case class Closed(issueId: IssueId) extends Event


trait Log {
  // Append the event to the log
  def append(events: List[Event]): Unit

  def fetch(issueId: IssueId): List[Event]
}

/**
  * Use cases
  */
case class Submit(
                   title: String,
                   authorId: UserId,
                   commentO: Option[String],
                   assignees: List[UserId],
                   milestones: List[MilestoneId],
                   categories: List[String]
                 )

case class Close(
                issueId: IssueId
                )

case class Comment(
                  issueId: IssueId,
                  comment: String
                  )

trait IssueWriteModel {
  def issueId: IssueId
}
case class Finished(issueId: IssueId) extends IssueWriteModel
case class Open(issueId: IssueId) extends IssueWriteModel

object IssueWriteModel {

  def empty(issueId: IssueId): IssueWriteModel = Open(issueId)

  def applySingle(state: IssueWriteModel, event: Event) = event match {
    case _ : Closed => Finished(event.issueId)
    case _          => state
  }

}

class IssueTracker(log: Log) {

  def handle(command: Submit): IssueWriteModel = {

    val id = IssueId(UUID.randomUUID().toString)

    // validate the command create events
    val events = validate(command, id)

    // persist event
    log.append(events)

    // create the view from the event
    createIssue(id, events)

  }

  def handle(command: Close): IssueWriteModel = {
    val events = log.fetch(command.issueId)
    val writeModel = events.foldLeft(IssueWriteModel.empty(command.issueId))(IssueWriteModel.applySingle)

    val eventO = writeModel match {
      case _ :Finished  => None
      case _            => Some(Closed(command.issueId))
    }

    if (eventO.isEmpty) throw new RuntimeException

    val e = eventO.get
    log.append(List(e))
    List(e).foldLeft(writeModel)(IssueWriteModel.applySingle)
  }

  def handle(command: Comment): IssueWriteModel = {
    val events = log.fetch(command.issueId)
    val writeModel = events.foldLeft(IssueWriteModel.empty(command.issueId))(IssueWriteModel.applySingle)

    val eventO = writeModel match {
      case _ :Finished  => None
      case _            => Some(Commented(command.issueId, command.comment))
    }

    if (eventO.isEmpty) throw new RuntimeException

    val e = eventO.get
    log.append(List(e))
    List(e).foldLeft(writeModel)(IssueWriteModel.applySingle)
  }

  def validate(command: Submit, id: IssueId) = {
    List.empty ++
      List(Created(id, command.authorId, command.title)) ++
      command.commentO.map(comment => List(Commented(id, comment))).getOrElse(List.empty) ++
      command.categories.map(label => Categorised(id, label)) ++
      command.assignees.map(userId => Assigned(id, userId)) ++
      command.milestones.map(milestoneId => Planned(id, milestoneId))
  }

  def createIssue(id: IssueId, events: List[Event]) = {
    events.foldLeft(IssueWriteModel.empty(id))(IssueWriteModel.applySingle)
  }
}