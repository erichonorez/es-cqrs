package complete.noes

import pprint._

object Demo1 extends App {

    val issue = IssueTrackerImpl.handle(Submit(
        "This is a issue",
        UserId("1324"),
        Some("This is a comment"),
        List(UserId("1234")),
        List(),
        List()
    ))

    pprintln(issue)


}

