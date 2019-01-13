package complete.simple
import java.util.UUID

import complete.share.{IssueId, MilestoneId, UserId}

object SimpleEsDemo extends App {

  val issueTracker = new IssueTracker(
    new Log {
      override def append(events: List[Event]): Unit = Unit
    }
  )


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
