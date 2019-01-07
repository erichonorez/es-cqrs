package complete.es

/**
  * A projection applies events on a given state.
  *
  * @tparam State type parameter
  * @tparam Event type parameter
  */
trait AggregateProjection[Id, State, Event] {
  /**
    * Applies a list of event on the given state.
    *
    * By default, this method is a `foldLeft` on the events. The start value is the given state and the binary operator is [[applySingle()]].
    *
    * @param state the actual state of the aggregate
    * @param events the events to apply on the state
    * @return the new state
    */
  def apply(state: State)(events: List[Event]): State = events.foldLeft(state)(applySingle)

  /**
    * Initialise an empty state for the aggregate identified by the given id.
    *
    * It is used as the start value for the [[apply()]] method.
    *
    * @param id the unique id of the aggregate.
    * @return an empty state
    */
  def initState(id: Id): State

  /**
    * Binary operator used in the `foldLeft` of [[apply()]].
    *
    * @param state the actual state of the aggregate
    * @param event the event to apply on the state
    * @return the new state
    */
  def applySingle(state: State, event: Event): State
}
