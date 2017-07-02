package homeworkzen.domain.command.message

import java.time.Instant

import homeworkzen.model._

sealed trait WithdrawError

object InvalidWithdrawAmount extends WithdrawError

object WithdrawExceedAvailableAmount extends WithdrawError

object WithdrawUnitNotFound extends WithdrawError

object WithdrawUserNotFound extends WithdrawError

case class WithdrawCommand(userId: UserId, unitId: UnitId, amountToWithdraw: Long) extends UnitCommand {
  val unitForwardFailureMessage = WithdrawResult(this, Left(WithdrawUnitNotFound))
  val userForwardFailureMessage = WithdrawResult(this, Left(WithdrawUserNotFound))
}

// error or new amount available
case class WithdrawResult(request: WithdrawCommand, result: Either[WithdrawError, Long])

case class WithdrawEvent(timestamp: Instant, userId: UserId, unitId: UnitId, amountWithdrawn: Long) extends UnitEvent