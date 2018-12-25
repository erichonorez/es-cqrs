package cqrs

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

