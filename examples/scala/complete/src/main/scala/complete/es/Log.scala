package complete.es

trait Log {
  // Append the event to the log
  def append(events: List[Event]): Unit
  // Fetch the events related to the given issue from the log
  def fetch(id: IssueId): List[Event]
}
