package incomplete.simple

import incomplete.share._

sealed trait Issue
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

  def empty(issueId: IssueId): Issue = ???

  def applySingle(state: Issue, event: Event): Issue = ???

}
