package complete.simple

import complete.noes.{IssueStatus, Open}
import complete.share.{IssueId, MilestoneId, UserId}

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

object Issue {

  def empty(issueId: IssueId): Issue = Empty(issueId)

  def applySingle(state: Issue, event: Event): Issue = state match {
    case _: Empty => event match {
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
