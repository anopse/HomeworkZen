package homeworkzen.domain.message

import homeworkzen.model._

sealed trait CreateUserError

object UsernameAlreadyExist extends CreateUserError

case class CreateUserCommand(username: String, hashedPassword: String)

case class CreateUserResult(request: CreateUserCommand, result: Either[CreateUserError, UserId])

case class UserCreatedEvent(userEntry: UserEntry)