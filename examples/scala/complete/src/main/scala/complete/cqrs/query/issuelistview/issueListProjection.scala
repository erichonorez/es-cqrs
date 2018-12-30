package complete.cqrs.query.issuelistview

import complete.cqrs._

case class Issue(
                issueId: IssueId,
                title: String,
                authorId: UserId
                )

class IssueListProjection extends Projection[Set[Issue]] {

  override def apply(state: Set[Issue])(events: List[Event]): Set[Issue] = {
    events.foldLeft(state)(applySingle)
  }

  override def initState: Set[Issue] = Set.empty

  private def applySingle(state: Set[Issue], event: Event): Set[Issue] = event match {
    // this projection only cares of Created event
    case Created(issueId, authorId, title) => {
      state.find(issue => issue.issueId == issueId) match {
        case None       => state + Issue(issueId, title, authorId) // add a new issue if not present
        case Some(i)    => state // return the current state if present
      }
    }
    case Closed(issueId) => {
      state.find(issue => issue.issueId == issueId) match {
        case Some(issue)  => state - issue
        case None         => state
      }
    }
    case _ => state // for the other kind of event we return the current state
  }
}
