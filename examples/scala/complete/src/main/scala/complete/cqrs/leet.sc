import complete.cqrs._ ;
import complete.cqrs.command._ ;
import complete.cqrs.query.issuelistview._ ;
import complete.cqrs.query.issueview._ ;


val log = new InMemoryLog ;
val identitySupplier = new UUIDNextId ;
val issueTracker = new IssueTracker(
  log,
  identitySupplier
) ;

val issueListView = new IssueListView(log) ;

val issueView = new IssueView(log) ;

issueTracker.handle(
  Submit(
    "Hello, World!",
    UserId("123"),
    None,
    List(),
    List(),
    List()
  )
)


issueTracker.handle(
  AddComment(
    IssueId("d8300201-2825-432b-8660-3a61e9032b17"),
    "This is my comment"
  )
)





