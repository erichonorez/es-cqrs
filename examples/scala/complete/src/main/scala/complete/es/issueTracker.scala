package complete.es

// commands
sealed trait Command
case class Submit(title: String, authorId: UserId, commentO: Option[String], assignees: List[UserId], milestones: List[MilestoneId], categories: List[String]) extends Command
case class AddComment(issueId: IssueId, comment: String) extends Command
/**
  * User cases implementation
  */
trait IssueTracker { this: Log with IdentitySupplier with AggregateProjection[Issue, IssueId] with IssueValidation =>

  def handle(command: Submit): Issue = {
    // Generate a new id for the issue
    val id = nextId
    // Instantiate an initial state for this id
    val initialState = initState(id)
    // validate the command against the initial state
    val events = validate(command, initialState)
    // append events to the log
    append(events)
    // return the application of events on the initial state
    apply(initialState)(events)
  }

  def handle(command: AddComment): Issue = {
    // fetch the events from the log
    val events = fetch(command.issueId)
    // if events exist for the issue id
    if (events.nonEmpty) {
      // recompute the state
      val state = apply(initState(command.issueId))(events)
      // validate the command against the actual state
      val newEvents = validate(command, state)
      // append new events to the log
      append(newEvents)
      // apply the events on the previous state
      val newState: Issue = apply(state)(newEvents)
      // return the actual state
      newState
    } else {
      // else return empty list
      Empty(command.issueId)
    }

  }

}
