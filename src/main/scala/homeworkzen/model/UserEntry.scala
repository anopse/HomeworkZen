package homeworkzen.model

/**
  * Case class containing basic information about user
  *
  * @param id       The id of the user (must never change)
  * @param username The username as used when authenticating
  * @param secret   The hashed password to verify against
  */
sealed case class UserEntry(id: UserId, username: String, secret: String)
