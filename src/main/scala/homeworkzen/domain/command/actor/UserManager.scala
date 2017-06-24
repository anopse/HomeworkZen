package homeworkzen.domain.command.actor

import java.time.Instant
import java.util.UUID

import akka.actor.Props
import akka.persistence.PersistentActor
import homeworkzen.domain.command.message._
import homeworkzen.model._

import scala.collection.mutable

sealed class UserManager extends PersistentActor {
  private val usernameToEntry: mutable.Map[String, UserEntry] = mutable.Map.empty

  override def receiveRecover: Receive = {
    case userCreated: UserCreatedEvent => apply(userCreated)
  }

  private def apply(userCreated: UserCreatedEvent): Unit = {
    val props = Props(new UserWorker(userCreated.userEntry.id))
    context.actorOf(props, userCreated.userEntry.id.id.toString)
    usernameToEntry += userCreated.userEntry.username -> userCreated.userEntry
  }

  override def receiveCommand: Receive = {
    case createUser: CreateUserCommand => handle(createUser)
    case getUserEntry: GetUserEntryRequest => handle(getUserEntry)
    case userCommand: UserCommand => forward(userCommand)
  }

  private def handle(createUser: CreateUserCommand): Unit = {
    if (usernameToEntry.contains(createUser.username)) {
      sender ! CreateUserResult(createUser, Left(UsernameAlreadyExist))
    } else {
      val userId = UserId(UUID.randomUUID())
      val originalSender = sender
      persist(UserCreatedEvent(Instant.now(), UserEntry(userId, createUser.username, createUser.hashedPassword))) { event =>
        apply(event)
        originalSender ! CreateUserResult(createUser, Right(userId))
      }
    }
  }

  private def forward(userCommand: UserCommand): Unit = {
    context.child(userCommand.userId.id.toString) match {
      case Some(child) => child forward userCommand
      case None => sender ! userCommand.userForwardFailureMessage
    }
  }

  private def handle(getUserEntry: GetUserEntryRequest): Unit = {
    usernameToEntry.get(getUserEntry.username) match {
      case Some(userEntry) => sender ! GetUserEntryResult(getUserEntry, Right(userEntry))
      case None => sender ! GetUserEntryResult(getUserEntry, Left(UsernameNotFound))
    }
  }

  override def persistenceId: String = "UserManager"
}
