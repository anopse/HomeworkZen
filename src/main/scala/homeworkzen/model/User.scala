package homeworkzen.model

import java.util.UUID

case class UserId(id: UUID)

case class UserEntry(id: UserId, username: String, secret: String)
