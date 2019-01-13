package complete.nviews

import complete.share.{IssueId, MilestoneId}

case class MilestoneIssuesView(
                                milestoneId: MilestoneId,
                                activeIssueCount: Int,
                                closedIssuesCount: Int,
                                percentageOfCompletion: Double,
                                openIssues: List[MilestoneIssue]
                              )

case class MilestoneIssue(
                         issueId: IssueId
                         )

object MilestoneIssuesView {

  def empty(milestoneId: MilestoneId) = MilestoneIssuesView(milestoneId, 0, 0, 0, List.empty)

  def applySingle(state: MilestoneIssuesView, event: Event): MilestoneIssuesView = {
    event match {
      case e: Planned => if (e.milestoneId == state.milestoneId) {
        state.copy(
          activeIssueCount = state.activeIssueCount + 1,
          percentageOfCompletion = state.closedIssuesCount.toFloat / (state.activeIssueCount + 1).toFloat,
          openIssues = state.openIssues :+ MilestoneIssue(e.issueId)
        )
      } else state
      case e: Closed  => if (state.openIssues.exists(i => i.issueId == e.issueId)) {
        state.copy(
          closedIssuesCount = state.closedIssuesCount + 1,
          percentageOfCompletion = (state.closedIssuesCount + 1).toFloat / state.activeIssueCount.toFloat,
          openIssues = state.openIssues.filter(i => i.issueId != e.issueId)
        )
      } else state
      case _ => state
    }
  }

}