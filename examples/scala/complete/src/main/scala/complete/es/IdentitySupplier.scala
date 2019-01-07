package complete.es

/**
  * An IdentitySupplier supplies new identifiers.
  */
trait IdentitySupplier {
  def nextId: IssueId
}
