package complete.nviews

case class ActiveIssuesView(
                           closedIssuesCount: Int,
                           openIssuesCount: Int,
                           totalOfIssues: Int
                           )

object ActiveIssuesView {

  def empty: ActiveIssuesView = ActiveIssuesView(0, 0, 0)

  def applySingle(state: ActiveIssuesView, event: Event): ActiveIssuesView = event match {
    case _: Created => state.copy(openIssuesCount = state.openIssuesCount + 1, totalOfIssues = state.totalOfIssues + 1)
    case _: Closed  => state.copy(closedIssuesCount = state.closedIssuesCount + 1, totalOfIssues = state.totalOfIssues + 1)
    case _ => state
  }

}
