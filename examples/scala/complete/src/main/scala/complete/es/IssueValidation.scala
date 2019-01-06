package complete.es

trait IssueValidation {

  def validate(command: Command, issue: Issue): List[Event] = command match {
    case c: Submit      => validateSubmit(c, issue)
    case c: AddComment  => validateAddComment(c, issue)
  }

  private def validateSubmit(command: Submit, issue: Issue): List[Event] = issue match {
    case Empty(id)  =>
      val appendCategorised = (es: List[Event]) => {
        val categorisedEvents = command.categories map((c: String) => Categorised(id, c))
        es ++ categorisedEvents
      }

      val appendAssigned = (es: List[Event]) => {
        val assignedEvents = command.assignees map((u: UserId) => Assigned(id, u))
        es ++ assignedEvents
      }

      val appendPlanned = (es: List[Event]) => {
        val plannedEvents = command.milestones map((m: MilestoneId) => Planned(id, m))
        es ++ plannedEvents
      }

      val appendCommented: List[Event] => List[Event] = (es: List[Event]) => {
        command.commentO match {
          case Some(comment) => es :+ Commented(id, comment)
          case _             => es
        }
      }

      val appendCreated = (es: List[Event]) => {
        es :+ Created(id, command.authorId, command.title)
      }

      val createAllEvents = appendCreated     andThen
        appendCommented   andThen
        appendPlanned     andThen
        appendAssigned    andThen
        appendCategorised

      createAllEvents(List())
    case _          => List()
  }

  private def validateAddComment(command: AddComment, issue: Issue): List[Event] = issue match {
    case s: Existing => List(
      Commented(command.issueId, command.comment)
    )
    case _ => List()
  }

}
