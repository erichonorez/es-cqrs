package incomplete.simple

import java.util.UUID

import incomplete.share.{MilestoneId, UserId}

object SimpleEsDemo extends App {

  val issueTracker = new IssueTracker()


  private val me = UserId(UUID.randomUUID.toString)
  private val issue: Issue = issueTracker.handle(Submit(
    "Hello DevFM",
    me,
    Some("A comment"),
    List(me),
    List(MilestoneId(UUID.randomUUID().toString)),
    List("good-first-issue")
  ))

  pprint.pprintln(issue)

}
