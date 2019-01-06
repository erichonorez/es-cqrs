package complete.nviews

import java.util.UUID

import complete.es._

object IssueByMilestoneDemo extends App {

  private val firstIssueId = IssueId(UUID.randomUUID().toString)
  private val secondIssueId = IssueId(UUID.randomUUID().toString)
  private val milestoneId = MilestoneId(UUID.randomUUID().toString)
  private val userId = UserId(UUID.randomUUID().toString)

  private val events = List(
    Created(
      firstIssueId,
      userId,
      "An issue"
    ),
    Planned(
      firstIssueId,
      milestoneId
    ),
    Created(
      secondIssueId,
      UserId(UUID.randomUUID().toString),
      "Another issue"
    ),
    Planned(
      secondIssueId,
      milestoneId
    ),
    Closed(
      firstIssueId,
      None
    )
  )

  val result = IssuesByMilestone.apply(IssuesByMilestone.initState)(events)
  pprint.pprintln(result.issuesByMilestone(milestoneId))

}
