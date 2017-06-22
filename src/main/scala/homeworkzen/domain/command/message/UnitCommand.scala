package homeworkzen.domain.command.message

import homeworkzen.model.UnitId

trait UnitCommand extends UserCommand {
  def unitId: UnitId

  def unitForwardFailureMessage: Any
}