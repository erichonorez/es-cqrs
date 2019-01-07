package complete.es

sealed trait IssueStatus
object Open extends IssueStatus
object Finished extends IssueStatus

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

trait IssueProjection extends AggregateProjection[IssueId, Issue, Event] {
  override def initState(issueId: IssueId) = Empty(issueId)

  override def applySingle(state: Issue, event: Event): Issue = state match {
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
