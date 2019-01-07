package complete.es

class CommentUseCase extends UseCase[AddComment, Issue, Event, IssueId] with IssueProjection {
  this: Log with IdentitySupplier =>
  /**
    * Execute the use case.
    *
    * @param c the parameter of the use case.
    * @return the updated state.
    */
  override def handle(c: AddComment): Issue = {
    // fetch the events from the log
    val events = fetch(c.issueId)
    // if events exist for the issue id
    if (events.nonEmpty) {
      // recompute the state
      val state = apply(initState(c.issueId))(events)
      // validate the command against the actual state
      val newEvents = validate(c)(state)
      // append new events to the log
      append(newEvents)
      // apply the events on the previous state
      val newState: Issue = apply(state)(newEvents)
      // return the actual state
      newState
    } else {
      // else return empty list
      Empty(c.issueId)
    }
  }

  /**
    * @param c the command to validate
    * @param s the actual state of the aggregate
    * @return the list of event
    */
  override def validate(c: AddComment)(s: Issue): List[Event] = s match {
    case _: Existing => List(
      Commented(c.issueId, c.comment)
    )
    case _ => List()
  }
}
