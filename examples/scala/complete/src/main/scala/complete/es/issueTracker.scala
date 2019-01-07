package complete.es

// commands
sealed trait Command
case class Submit(title: String, authorId: UserId, commentO: Option[String], assignees: List[UserId], milestones: List[MilestoneId], categories: List[String]) extends Command
case class AddComment(issueId: IssueId, comment: String) extends Command

/**
  * User cases API
  */
class IssueTrackerApi(submitUseCase: SubmitUseCase, commentUseCase: CommentUseCase) {

  def handle(c: Command): Issue = c match {
    case t: Submit      => submitUseCase.handle(t)
    case t: AddComment  => commentUseCase.handle(t)
  }

}

