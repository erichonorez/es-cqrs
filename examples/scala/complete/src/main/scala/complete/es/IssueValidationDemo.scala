package complete.es

import java.util.UUID

object IssueValidationDemo extends App with IssueValidation {

  private val issueId = IssueId(UUID.randomUUID().toString)
  private val authorId = UserId(UUID.randomUUID().toString)
  private val milestoneId = MilestoneId(UUID.randomUUID().toString)

  private val events: List[Event] = validate(
    Submit(
      "a tittle",
      authorId,
      Some("A comment"),
      List(authorId),
      List(milestoneId),
      List("A label")
    ),
    Empty(issueId)
  )

  pprint.pprintln(events)

}
