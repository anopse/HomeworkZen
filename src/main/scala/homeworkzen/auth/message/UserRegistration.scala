package homeworkzen.auth.message

import homeworkzen.model.UserEntry

sealed trait UserRegistrationError

object UsernameAlreadyExist extends UserRegistrationError

object PasswordRequirementFailed extends UserRegistrationError

object UsernameRequirementFailed extends UserRegistrationError

case class UserRegistrationRequest(username: String, password: String)

case class UserRegistrationResult(request: UserRegistrationRequest, result: Either[UserRegistrationError, UserEntry])
