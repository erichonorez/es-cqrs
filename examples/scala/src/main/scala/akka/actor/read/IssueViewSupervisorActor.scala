package akka.actor.read

import akka.actor.{Actor, Props}
import cqrs._

class IssueViewSupervisorActor extends Actor {

  override def receive: Receive = {
    case e: Created     => handleCreated(e)
    case e: Commented   => delegate(e)
    case e: Assigned    => delegate(e)
    case e: Planned     => delegate(e)
    case e: Categorised => delegate(e)
    case e: Closed      => delegate(e)
    case c: GetView         => get(c)
  }

  private def handleCreated(e: Created): Unit = {
    val issueId = e.issueId
    val actorRef = context.actorOf(IssueViewActor.props(issueId), issueId.value)
    actorRef.forward(e)
  }

  private def delegate(event: Event): Unit = {
    context.child(event.issueId.value)
      .foreach(actor => actor.forward(event))
  }

  def get(c: GetView): Unit = {
    context.child(c.issueId.value)
      .foreach(actor => actor.forward(c))
  }
}

object IssueViewSupervisorActor {

  def props = Props(classOf[IssueViewSupervisorActor])

}

case class GetView(issueId: IssueId)
