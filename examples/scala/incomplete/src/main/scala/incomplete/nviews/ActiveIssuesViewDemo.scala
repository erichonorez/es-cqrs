package incomplete.nviews

import java.util.UUID

import incomplete.share.{IssueId, UserId}

object ActiveIssuesViewDemo extends App {

  private val issueId = IssueId(UUID.randomUUID().toString)
  private val authorId = UserId(UUID.randomUUID().toString)
  private val issueId2 = IssueId(UUID.randomUUID().toString)

  val events: List[Event] = List(
    Created(
      issueId,
      authorId,
      "A title"
    ),
    Commented(
      issueId,
      "A comment"

    ),
    Categorised(
      issueId,
      "improvement"
    ),
    Assigned(
      issueId,
      authorId
    ),
    Created(
      issueId2,
      authorId,
      "A title"
    ),
    Commented(
      issueId2,
      "A comment"

    ),
    Categorised(
      issueId2,
      "improvement"
    ),
    Assigned(
      issueId2,
      authorId
    ),
    Closed(issueId)
  )

  private val view: ActiveIssuesView = events.foldLeft(ActiveIssuesView.empty)(ActiveIssuesView.applySingle)

  pprint.pprintln(view)

}
