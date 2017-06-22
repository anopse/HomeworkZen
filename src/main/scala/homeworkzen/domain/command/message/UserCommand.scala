package homeworkzen.domain.command.message

import homeworkzen.model.UserId

trait UserCommand {
  def userId: UserId

  def userForwardFailureMessage: Any
}
