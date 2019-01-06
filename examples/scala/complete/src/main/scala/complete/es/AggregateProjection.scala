package complete.es

// Projection
trait AggregateProjection[S, U] {
  def apply(state: S)(events: List[Event]): S = events.foldLeft(state)(applySingle)
  def initState(id: U): S
  def applySingle(state: S, event: Event): S
}
