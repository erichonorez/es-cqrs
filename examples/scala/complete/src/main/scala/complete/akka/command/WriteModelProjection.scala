package complete.akka.command

import complete.cqrs._

sealed trait WriteModel
case class Empty(id: IssueId) extends WriteModel
case class Open(id: IssueId) extends WriteModel
case class Resolved(id: IssueId) extends WriteModel

class WriteModelProjection {
  def apply(state: WriteModel)(events: List[Event]): WriteModel = {
    events.foldLeft(state)(applySingle)
  }

  def initState(issueId: IssueId): WriteModel = Empty(issueId)

  private def applySingle(state: WriteModel, event: Event): WriteModel = state match {
    case Empty(id) => event match { // Only the event Created can transform Empty -> Open
      case Created(issueId, _, _) => Open(issueId)
      case _ => Empty(id) // otherwise the state remains Empty
    }
    case s: Open => event match { // When the issue is open and has been closed -> Resolved
      case Closed(issueId) => Resolved(issueId)
      case _ => s // otherwise the state remains the same
    }
    case s => s
  }
}
