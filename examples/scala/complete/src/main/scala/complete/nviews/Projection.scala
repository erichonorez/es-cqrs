package complete.nviews

import complete.es.Event

// Projection
trait Projection[S] {
  def apply(state: S)(events: List[Event]): S = events.foldLeft(state)(applySingle)
  def initState: S
  def applySingle(state: S, event: Event): S
}
