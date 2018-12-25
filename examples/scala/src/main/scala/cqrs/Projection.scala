package cqrs

trait Projection[S] {
  def apply(state: S)(events: List[Event]): S
  def initState: S
}
