package complete.simple

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


trait Log {
  // Append the event to the log
  def append(events: List[Event]): Unit
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

class IssueTracker(log: Log) {

  def handle(command: Submit): Issue = {

    val id = IssueId(UUID.randomUUID().toString)

    // validate the command create events
    val events = validate(command, id)

    // persist event
    log.append(events)

    // create the view from the event
    createIssue(id, events)

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
    events.foldLeft(Issue.empty(id))(Issue.applySingle)
  }

}