package homeworkzen.auth.message

import homeworkzen.model.UserEntry

sealed trait GetUserEntryError

object UsernameNotFound extends GetUserEntryError

case class GetUserEntryRequest(username: String)

case class GetUserEntryResult(request: GetUserEntryRequest, result: Either[GetUserEntryError, UserEntry])


