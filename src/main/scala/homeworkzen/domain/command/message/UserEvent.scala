package homeworkzen.domain.command.message

import homeworkzen.model.UserId

trait UserEvent extends Event {
  def userId: UserId
}
