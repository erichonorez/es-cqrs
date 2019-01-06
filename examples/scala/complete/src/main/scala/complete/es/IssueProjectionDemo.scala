package complete.es

import java.util.UUID

object IssueProjectionDemo extends App with IssueProjection {

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

  private val issue: Issue = apply(initState(issueId))(events)

  pprint.pprintln(issue)

}
