package akka

import akka.pattern.ask
import akka.actor.{ActorRef, ActorSystem}
import akka.actor.read.{GetView, IssueViewSupervisorActor}
import akka.actor.write.IssueTrackerSupervisorActor
import cqrs._
import akka.command._
import akka.query.Issue
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object App {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("cqrs-scala")

    val issueViewSupervisor = system.actorOf(IssueViewSupervisorActor.props)
    system.eventStream.subscribe(issueViewSupervisor, classOf[Event])

    val issueTrackerSupervisor = system.actorOf(IssueTrackerSupervisorActor.props)

    for {
      issueId <- submitIssue(issueTrackerSupervisor)
      view <- retriveView(issueViewSupervisor, issueId)
    } print(view)

  }

  private def submitIssue(issueTrackerSupervisor: ActorRef) = {
    implicit val timeout = Timeout(5 seconds)

    (issueTrackerSupervisor ? Submit(
      "Hello, World",
      UserId("123"),
      None,
      List(),
      List(),
      List()
    )).mapTo[IssueId]
  }

  private def retriveView(issueViewSupervisor: ActorRef, issueId: IssueId) = {
    implicit val timeout = Timeout(5 seconds)

    (issueViewSupervisor ? GetView(issueId)).mapTo[Issue]
  }
}
