package homeworkzen.model

import java.util.UUID

/** Case class containing basic information about user
  *
  * @param id       The UUID of the user (an user id must never change)
  * @param username The username as used when authenticating
  * @param secret   The hashed password to verify against
  */
case class UserEntry(id: UUID, username: String, secret: String)
