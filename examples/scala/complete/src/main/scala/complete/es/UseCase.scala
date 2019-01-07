package complete.es

/**
  * A use case creates or modifies an aggregate.
  *
  * @tparam Command the type of command the use case handle.
  * @tparam State the type of state the use case manipulate.
  * @tparam Event the type of event the use case generate.
  * @tparam Id the type of identifier of the the aggregate root.
  */
trait UseCase[Command, State, Event, Id] extends CommandValidator[Command, State, Event] {
  this: AggregateProjection[Id, State, Event] =>

  /**
    * Execute the use case.
    *
    * @param c the parameter of the use case.
    * @return the updated state.
    */
  def handle(c: Command): State
}
