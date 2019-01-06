package complete.nviews

import java.util.UUID

import complete.es._

object IssueByIdDemo extends App with IssueProjection {

  private val issueId = IssueId(UUID.randomUUID().toString)
  private val authorId = UserId(UUID.randomUUID().toString)

  val events = List(
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
    )
  )

  private val issue: Issue = apply(initState)(events)
  pprint.pprintln(issue)

}
