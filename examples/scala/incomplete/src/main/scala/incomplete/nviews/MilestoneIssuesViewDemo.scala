package incomplete.nviews

import java.util.UUID

import incomplete.share.{IssueId, MilestoneId, UserId}

object MilestoneIssuesViewDemo extends App {

  private val issueId = IssueId(UUID.randomUUID().toString)
  private val authorId = UserId(UUID.randomUUID().toString)
  private val milestoneId = MilestoneId(UUID.randomUUID().toString)
  private val issueId2 = IssueId(UUID.randomUUID().toString)

  val events: List[Event] = List(
    Created(
      issueId,
      authorId,
      "A title"
    ),
    Planned(
      issueId,
      milestoneId
    ),
    Created(
      issueId2,
      authorId,
      "A title"
    ),
    Planned(
      issueId2,
      milestoneId
    ),
    Closed(issueId)
  )

  val state = events.foldLeft(MilestoneIssuesView.empty(milestoneId))(MilestoneIssuesView.applySingle)

  pprint.pprintln(state)

}
