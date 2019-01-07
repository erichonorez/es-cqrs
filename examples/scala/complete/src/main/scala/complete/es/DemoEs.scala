package complete.es

import java.util.UUID

object DemoEs extends App {


}

trait InMemoryLog extends Log {

  private var records: List[Event] = List()

  def append(events: List[Event]): Unit = {
    records = records ++ events
  }

  def fetch(issueId: IssueId): List[Event] = {
    records filter ((e: Event) => e.issueId == issueId)
  }

}

trait UUIDNextId extends IdentitySupplier {
  def nextId = IssueId(UUID.randomUUID().toString)
}
