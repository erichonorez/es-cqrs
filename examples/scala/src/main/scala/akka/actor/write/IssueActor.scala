package akka.actor.write

import akka.actor.{Actor, Props}
import akka.command._
import cqrs.IssueId

class IssueActor(issueId: IssueId) extends Actor {

  val writeModelProjection: WriteModelProjection = new WriteModelProjection()

  var state = writeModelProjection.initState(issueId)

  override def receive: Receive = {
    case c: Submit      => handleSubmit(c)
    case c: AddComment  => handleAddComment(c)
    case c: Close       => handleClose(c)
  }

  private def handleSubmit(c: Submit): Unit = {
    val events = IssueTracker.handle(c)(state)
    state = writeModelProjection.apply(state)(events)

    events.foreach(context.system.eventStream.publish(_))
  }

  private def handleAddComment(c: AddComment): Unit = {
    val events = IssueTracker.handle(c)(state)
    state = writeModelProjection.apply(state)(events)

    context.system.eventStream.publish(events)
  }

  private def handleClose(c: Close): Unit = {
    val events = IssueTracker.handle(c)(state)
    state = writeModelProjection.apply(state)(events)

    context.system.eventStream.publish(events)
  }

}

object IssueActor {

  def props(issueId: IssueId): Props = Props(classOf[IssueActor], issueId)

}
