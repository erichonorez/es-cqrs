package complete.es

/**
  * Validates a command against a given state. The result is a list of event.
  *
  *
  * @tparam Command the command type
  * @tparam State the state type
  * @tparam Event event type
  */
trait CommandValidator[Command, State, Event] {

  /**
    * @param c the command to validate
    * @param s the actual state of the aggregate
    * @return the list of event
    */
  def validate(c: Command)(s: State): List[Event]

}
