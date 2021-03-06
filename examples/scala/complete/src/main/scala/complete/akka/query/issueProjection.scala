package complete.akka.query

import complete.cqrs._

trait IssueStatus
object Open extends IssueStatus
object Closed extends IssueStatus

sealed trait Issue
case class Empty(id: IssueId) extends Issue
case class Existing(
                     id: IssueId,
                     title: String,
                     author: UserId,
                     status: IssueStatus,
                     comments: List[String],
                     assignees: List[UserId],
                     milestones: List[MilestoneId],
                     labels: List[String]
                   ) extends Issue


class IssueProjection {
  def apply(state: Issue)(events: List[Event]): Issue = {
    events.foldLeft(state)(applySingle)
  }

  def initState(issueId: IssueId): Issue = Empty(issueId)

  private def applySingle(state: Issue, event: Event): Issue = state match {
    case Empty(_) => event match {
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
    case s: Existing => event match {
      case Commented(_, comment) => s.copy(comments = s.comments :+ comment)
      case Assigned(_, assigneeId) => s.copy(assignees = s.assignees :+ assigneeId)
      case Planned(_, milestoneId) => s.copy(milestones = s.milestones :+ milestoneId)
      case Categorised(_, label) => s.copy(labels = s.labels :+ label)
      case _ => throw new IllegalStateException
    }
  }
}
