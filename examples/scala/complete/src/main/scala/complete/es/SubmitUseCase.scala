package complete.es

class SubmitUseCase extends UseCase[Submit, Issue, Event, IssueId] with IssueProjection {
  this: Log with IdentitySupplier =>
  /**
    * Execute the use case.
    *
    * @param c the parameter of the use case.
    * @return the updated state.
    */
  override def handle(c: Submit): Issue = {
    // Generate a new id for the issue
    val id = nextId
    // Instantiate an initial state for this id
    val initialState = initState(id)
    // validate the command against the initial state
    val events = validate(c)(initialState)
    // append events to the log
    append(events)
    // return the application of events on the initial state
    apply(initialState)(events)
  }

  /**
    * @param c the command to validate
    * @param s the actual state of the aggregate
    * @return the list of event
    */
  override def validate(c: Submit)(s: Issue): List[Event] = s match {
    case Empty(id)  =>
      val appendCategorised = (es: List[Event]) => {
        val categorisedEvents = c.categories map((c: String) => Categorised(id, c))
        es ++ categorisedEvents
      }

      val appendAssigned = (es: List[Event]) => {
        val assignedEvents = c.assignees map((u: UserId) => Assigned(id, u))
        es ++ assignedEvents
      }

      val appendPlanned = (es: List[Event]) => {
        val plannedEvents = c.milestones map((m: MilestoneId) => Planned(id, m))
        es ++ plannedEvents
      }

      val appendCommented: List[Event] => List[Event] = (es: List[Event]) => {
        c.commentO match {
          case Some(comment) => es :+ Commented(id, comment)
          case _             => es
        }
      }

      val appendCreated = (es: List[Event]) => {
        es :+ Created(id, c.authorId, c.title)
      }

      val createAllEvents = appendCreated     andThen
        appendCommented   andThen
        appendPlanned     andThen
        appendAssigned    andThen
        appendCategorised

      createAllEvents(List())
    case _          => List()
  }
}
