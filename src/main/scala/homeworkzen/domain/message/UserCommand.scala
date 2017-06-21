package homeworkzen.domain.message

import homeworkzen.model.UserId

trait UserCommand {
  def userId: UserId

  def userForwardFailureMessage: Any
}
