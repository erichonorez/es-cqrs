package incomplete.simple

import incomplete.share.{Issue, MilestoneId, UserId}

// events


// Log

/**
  * Use cases
  */
/**
  * Use cases
  */
case class Submit(
                   title: String,
                   authorId: UserId,
                   commentO: Option[String],
                   assignees: List[UserId],
                   milestones: List[MilestoneId],
                   categories: List[String]
                 )

class IssueTracker {

  def handle(command: Submit): Issue = ???

}