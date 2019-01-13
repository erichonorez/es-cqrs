package incomplete.nviews

case class ActiveIssuesView(
                           closedIssuesCount: Int,
                           openIssuesCount: Int,
                           totalOfIssues: Int
                           )

object ActiveIssuesView {

  def empty: ActiveIssuesView = ???

  def applySingle(state: ActiveIssuesView, event: Event): ActiveIssuesView = ???

}
