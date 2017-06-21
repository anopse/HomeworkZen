package homeworkzen.domain.message

import homeworkzen.model._

sealed trait WithdrawError

object InvalidWithdrawAmount extends WithdrawError

object WithdrawExceedAvailableAmount extends WithdrawError

object WithdrawUnitNotFound extends WithdrawError

object WithdrawUserNotFound extends WithdrawError

case class WithdrawCommand(userId: UserId, unitId: UnitId, amountToWithdraw: Long) extends UnitCommand {
  val unitForwardFailureMessage = WithdrawUnitNotFound
  val userForwardFailureMessage = WithdrawUserNotFound
}

// todo : document somehow that Long result is the new amount available
case class WithdrawResult(request: WithdrawCommand, result: Either[WithdrawError, Long])

case class WithdrawEvent(unitId: UnitId, amountWithdrawn: Long)