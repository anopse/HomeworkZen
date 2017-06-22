package homeworkzen.domain.command.message

import homeworkzen.model.{UnitId, UserId}

// used for tagging unit event with user id
trait UnitEvent {
  def userId: UserId

  def unitId: UnitId
}
