package complete.cqrs.query.issuelistview

import complete.cqrs.Log

class IssueListView(log: Log) {

  private val projection = new IssueListProjection

  def get: List[Issue] = projection.apply(projection.initState)(log.all) toList

}
