package cqrs.command

import cqrs._

trait IdentitySupplier {
  def nextId: IssueId
}
