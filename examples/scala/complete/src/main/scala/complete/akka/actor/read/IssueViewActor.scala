package complete.akka.actor.read

import complete.cqrs._
import akka.actor.{Actor, Props}
import complete.akka.query.{Issue, IssueProjection}

case class Get(issueId: IssueId)

class IssueViewActor(issueId: IssueId) extends Actor {

  val issueProjection = new IssueProjection

  var view: Issue = issueProjection.initState(issueId)

  override def receive: Receive = {
    case e: Created     => apply(e)
    case e: Commented   => apply(e)
    case e: Assigned    => apply(e)
    case e: Planned     => apply(e)
    case e: Categorised => apply(e)
    case e: Closed      => apply(e)
    case GetView(_)     => sender() ! view
  }


  def apply(e: Event): Unit = {
    view = issueProjection.apply(view)(List(e))
  }
}

object IssueViewActor {

  def props(issueId: IssueId): Props = Props(classOf[IssueViewActor], issueId)

}
