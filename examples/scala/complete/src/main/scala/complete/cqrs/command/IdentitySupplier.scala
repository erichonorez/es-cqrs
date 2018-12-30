package complete.cqrs.command

import complete.cqrs._

trait IdentitySupplier {
  def nextId: IssueId
}
