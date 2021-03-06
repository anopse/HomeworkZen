package homeworkzen.domain.command.message

import java.time.Instant

import homeworkzen.model._

sealed trait CreateUserError

object UsernameAlreadyExist extends CreateUserError

case class CreateUserCommand(username: String, hashedPassword: String)

case class CreateUserResult(request: CreateUserCommand, result: Either[CreateUserError, UserId])

case class UserCreatedEvent(timestamp: Instant, userEntry: UserEntry) extends UserEvent {
  override def userId: UserId = userEntry.id
}