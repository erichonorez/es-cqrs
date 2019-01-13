package complete.simplecqrs
import java.util.UUID

import complete.share.{IssueId, UserId}
import complete.simple.Issue

import scala.collection.mutable

object IssueTrackerDemo extends App {

  val log = new Log {

    val log: mutable.MutableList[Event] = mutable.MutableList.empty

    override def append(events: List[Event]): Unit = events.foreach(e => log += e)

    override def fetch(issueId: IssueId): List[Event] = log.filter(e => e.issueId == issueId).toList
  }

  val issueTracker = new IssueTracker(log)


  val model = issueTracker.handle(Submit(
    "Hello",
    UserId(UUID.randomUUID().toString),
    None,
    List(),
    List(),
    List()
  ))

  issueTracker.handle(Close(model.issueId))


  issueTracker.handle(Comment(
    model.issueId,
    "A comment"
  ))
}
