package incomplete.nviews

import incomplete.share.{IssueId, MilestoneId}

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

  def applySingle(state: MilestoneIssuesView, event: Event): MilestoneIssuesView = ???

}