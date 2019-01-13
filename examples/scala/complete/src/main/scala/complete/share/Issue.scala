package complete.share

import complete.noes.IssueStatus

case class Issue(
                  id: IssueId,
                  title: String,
                  author: UserId,
                  status: IssueStatus,
                  comments: List[String],
                  assignees: List[UserId],
                  milestones: List[MilestoneId],
                  labels: List[String]
                )