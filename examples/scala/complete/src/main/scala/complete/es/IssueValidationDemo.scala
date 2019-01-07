package complete.es

import java.util.UUID

object IssueValidationDemo extends App with InMemoryLog with UUIDNextId {

  val submitUseCase = new SubmitUseCase with InMemoryLog with UUIDNextId
  val commentUseCase = new CommentUseCase with InMemoryLog with UUIDNextId


  private val issueId = IssueId(UUID.randomUUID().toString)
  private val authorId = UserId(UUID.randomUUID().toString)
  private val milestoneId = MilestoneId(UUID.randomUUID().toString)

  private val initState = Empty(issueId)

  private val events1: List[Event] = submitUseCase.validate(
    Submit(
      "a tittle",
      authorId,
      Some("A comment"),
      List(authorId),
      List(milestoneId),
      List("A label")
    ))(initState)

  println(
    """
      | Submit an Issue use case validation
    """.stripMargin)

  pprint.pprintln(events1)

  println(
    """
      | Add a comment use case validation
    """.stripMargin)

  val state1 = submitUseCase.apply(initState)(events1)

  private val events2: List[Event] = commentUseCase.validate(AddComment(
    issueId,
    "This is another comment"
  ))(state1)

  pprint.pprintln(events2)

}
