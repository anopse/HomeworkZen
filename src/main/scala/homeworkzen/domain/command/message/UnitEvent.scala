package homeworkzen.domain.command.message

import homeworkzen.model.UserId

// used for tagging unit event with user id
trait UnitEvent {
  def userId: UserId
}
