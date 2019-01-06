package complete.nviews

import complete.es._

import scala.collection.mutable
import scala.collection.mutable.Map

class ViewState(
               var issuesByMilestone: mutable.Map[MilestoneId, MilestoneIssues],
               var allIssues: mutable.Map[IssueId, (MilestoneIssue, mutable.MutableList[MilestoneId])]
               )

case class MilestoneIssues(
                           openIssuesCount: Int,
                           closedIssuesCount: Int,
                           percentageOfCompletion: Float,
                           issues: mutable.MutableList[MilestoneIssue]
                           )

case class MilestoneIssue(issueId: IssueId, title: String)

object IssuesByMilestone extends Projection[ViewState] {

  override def apply(state: ViewState)(events: List[Event]): ViewState = {
    events.foldLeft(state)((s, e) => applySingle(s, e))
  }

  override def initState: ViewState = new ViewState(
    issuesByMilestone = Map.empty,
    allIssues = Map.empty
  )

  private def applySingle(s: ViewState, e: Event): ViewState = e match {
    case Created(issueId, _, title) => {
      s.allIssues += (issueId -> (MilestoneIssue(issueId, title), mutable.MutableList.empty))
      s
    }
    case Planned(issueId, milestoneId) => {
      val issuesByMilestone = s.issuesByMilestone.getOrElseUpdate(milestoneId, MilestoneIssues(
        openIssuesCount = 0,
        closedIssuesCount = 0,
        percentageOfCompletion = 0,
        issues = mutable.MutableList.empty
      ))

      val issue = s.allIssues(issueId)
      val openIssueCount = issuesByMilestone.openIssuesCount + 1
      val updatedIssueByMilestone = issuesByMilestone.copy(
        openIssuesCount = openIssueCount,
        percentageOfCompletion = issuesByMilestone.closedIssuesCount / openIssueCount,
        issues = issuesByMilestone.issues :+ issue._1
      )

      issue._2 += milestoneId

      s.issuesByMilestone += (milestoneId -> updatedIssueByMilestone)
      s.allIssues += (issueId -> issue)
      s
    }
    case Closed(issueId, _) =>
      s.allIssues(issueId)._2.foreach(id => {
        val milestone = s.issuesByMilestone(id)
        s.issuesByMilestone -= id

        val closedIssueCount = milestone.closedIssuesCount + 1
        val updatedMilestone = milestone.copy(
          closedIssuesCount = closedIssueCount,
          percentageOfCompletion = closedIssueCount.toFloat / milestone.openIssuesCount.toFloat,
          issues = milestone.issues filter (i => i.issueId != issueId)
        )

        s.issuesByMilestone += (id -> updatedMilestone)
      })
      s

    case _ => s
  }
}