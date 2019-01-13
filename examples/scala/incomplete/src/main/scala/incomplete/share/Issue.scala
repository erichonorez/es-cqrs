package incomplete.share

trait IssueStatus
object Open extends IssueStatus
object Closed extends IssueStatus

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