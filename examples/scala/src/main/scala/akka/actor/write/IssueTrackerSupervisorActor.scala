package akka.actor.write

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.command._
import cqrs.IssueId

class IssueTrackerSupervisorActor extends Actor {

  override def receive: Receive = {
    case c: Submit      => handleSubmit(c)
    case c: AddComment  => delegateAddComment(c)
    case c: Close       => delegateClose(c)
  }

  def handleSubmit(command: Submit): Unit = {
    val untypedId = UUID.randomUUID().toString
    val id = IssueId(untypedId)
    val actor = context.actorOf(IssueActor.props(id), untypedId)
    actor.forward(command)
    sender() ! id
  }

  def delegateAddComment(command: AddComment): Unit = {
    context.child(command.issueId.value)
      .foreach(actor => actor.forward(command))
  }

  def delegateClose(command: Close): Unit = {
    context.child(command.issueId.value)
      .foreach(actor => actor.forward(command))
  }

}

object IssueTrackerSupervisorActor {

  def props = Props(classOf[IssueTrackerSupervisorActor])

}
