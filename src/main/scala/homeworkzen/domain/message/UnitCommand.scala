package homeworkzen.domain.message

import homeworkzen.model.UnitId

trait UnitCommand extends UserCommand {
  def unitId: UnitId

  def unitForwardFailureMessage: Any
}