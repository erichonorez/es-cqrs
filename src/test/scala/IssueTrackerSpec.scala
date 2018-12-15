import org.scalatest.FreeSpec
import org.scalatest.Matchers._

class IssueTrackerSpec extends FreeSpec with IssueTracker {

  "An issue tracker" - {

    val title = "This is my title"
    val defaultSubmit = Submit(title, None, List(), List(), List())
    val initialState = Empty(IssueId("123"))

    "when the user submit an issue" - {

      "an issue should be created" - {

        val result = handle(defaultSubmit)(initialState)
        contains(created, result) should be (true)

      }

      "and the user add a comment" - {

        "then the issue should be commented" in {

          val command = defaultSubmit.copy(commentO = Some("This is my comment"))
          val result = handle(command)(initialState)
          contains(commented, result) should be (true)

        }

      }

      "and the user add a milestone" - {

        "then the issue should be planned" in {

          val command = defaultSubmit.copy(milestones = List(MilestoneId("123")))
          val result = handle(command)(initialState)
          contains(planned, result) should be (true)
        }

      }

      "and the user add an assignee" - {

        "then the issue should be assigned" in {

          val command = defaultSubmit.copy(assignees = List(UserId("213")))
          val result = handle(command)(initialState)
          contains(assigned, result) should be (true)

        }

      }

      "and the user add an label" - {

        "then the issue should be categorised" in {

          val command = defaultSubmit.copy(categories = List("todo"))
          val result = handle(command)(initialState)
          contains(categorised, result) should be (true)

        }

      }

    }

  }

  val existingIssue = Existing(IssueId("123"))

  "when the user add a comment to an issue" - {

    "then the issue should be commented" in {

      val command = AddComment("This is another event in the conversation")
      val result = handle(command)(existingIssue)
      contains(commented, result) should be (true)

    }

  }

  def contains(f: Event => Boolean, result: (List[Event], State)) = {
    (result._1 find f) isDefined
  }

  def created(x: Event) = x match {
    case Created(_, _) => true
    case _             => false
  }

  def commented(x: Event) = x match {
    case Commented(_, _) => true
    case _               => false
  }

  def planned(x: Event) = x match {
    case Planned(_, _) => true
    case _             => false
  }

  def assigned(x: Event) = x match {
    case Assigned(_, _) => true
    case _              => false
  }

  def categorised(x: Event) = x match {
    case Categorised(_, _) => true
    case _                 => false
  }
}
