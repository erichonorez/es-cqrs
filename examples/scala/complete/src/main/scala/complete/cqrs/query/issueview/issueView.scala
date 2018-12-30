package complete.cqrs.query.issueview

import complete.cqrs.{Log, _}

case class Get(issueId: IssueId)

class IssueView(log: Log) {

  private val issueProjection = new IssueProjection

  def handle(query: Get) = issueProjection.apply(issueProjection.initState)(log.fetch(query.issueId))

}



