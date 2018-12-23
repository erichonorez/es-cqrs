package noes

import java.util.UUID

/**
  * This is an example of an issue tracker implementation without event sourcing.
  */
trait IssueStatus
object Open extends IssueStatus
object Closed extends IssueStatus

case class IssueId(value: String)
case class UserId(value: String)
case class MilestoneId(value: String)

/**
  * Issue is an entity. It is fetched and persisted from the database using a {@link Issuetrackerrepository}.
  */
case class Issue(
  id: IssueId,
  title: String,
  author: UserId,
  status: IssueStatus,
  comments: List[String],
  assigneees: List[UserId],
  milestones: List[MilestoneId],
  labels: List[String]
)

/**
  * Interface for issue related database operations.
  */
trait IssueTrackerRepository {

  def nextId(): IssueId
  def persist(issue: Issue): Unit
  def update(issue: Issue): Unit
  def find(id: IssueId): Issue

}

/**
  * Use cases implementation
  */
trait IssueTracker { this: IssueTrackerRepository =>

  def handle(command: Submit): Issue = {

    // Validates the input parameters (author exists, repository exists and other business rules)
    // ..

    // Once validated create and issue
    val issue = Issue(
      nextId(), // <-- assigning a new issue identifier and a title and a author to this model implicitly means that a issue has been created
      command.title,
      command.authorId,
      Open,                                                           // <-- implicitly means the issue has been closed yet
      command.commentO.map ((c: String) => List(c)) getOrElse List(), // <-- implicitly means the issue has been commented
      command.assignees,                                              // <-- implicitly means the issue has been assigned
      command.milestones,                                             // <-- implicitly means the issue has been planned
      command.categories                                              // <-- implicitly means the issue has been categorized
    )

    // persist it
    persist(issue)

    // return it
    issue // <-- implicitly means all the events have been successfully persisted

  }

  def handle(command: AddComment): Issue = {

    // fetch the issue from the database
    val issue = find(command.issueId)

    // apply business rules and append comment
    val updatedIssue = if (issue.status == Open) {
      issue.copy(comments = issue.comments :+ command.comment) // <-- implicitly means the issue has been commented
    } else {
      // we surely return an error or throw an exception if the issue is closed
      issue
    }

    // persist it
    update(updatedIssue)

   // return the persisted issue
    updatedIssue // <-- returning the issue means that it has been commented
                 // <-- but what if the reality is more complex ?

  }

}

case class Submit(title: String, authorId: UserId, commentO: Option[String], assignees: List[UserId], milestones: List[MilestoneId], categories: List[String])
case class AddComment(issueId: IssueId, comment: String)


trait InMemoryIssueTrackerRepository extends IssueTrackerRepository {

  var issues: Map[IssueId, Issue] = Map()

  override def nextId() = IssueId(UUID.randomUUID().toString)
  override def persist(issue: Issue) {
    issues = issues + (issue.id -> issue)
  }
  override def update(issue: Issue) = persist _
  override def find(issueId: IssueId) = issues get issueId get
}

object IssueTrackerImpl extends IssueTracker with InMemoryIssueTrackerRepository { }
