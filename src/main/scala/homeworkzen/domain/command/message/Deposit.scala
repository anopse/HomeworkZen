package homeworkzen.domain.command.message

import java.time.Instant

import homeworkzen.model._

sealed trait DepositError

object InvalidDepositAmount extends DepositError

object DepositExceedCapacity extends DepositError

object DepositUserNotFound extends DepositError

object DepositUnitNotFound extends DepositError

case class DepositCommand(userId: UserId, unitId: UnitId, amountToDeposit: Long) extends UnitCommand {
  val unitForwardFailureMessage = DepositResult(this, Left(DepositUnitNotFound))
  val userForwardFailureMessage = DepositResult(this, Left(DepositUserNotFound))
}

// error or new amount available
case class DepositResult(request: DepositCommand, result: Either[DepositError, Long])

case class DepositEvent(timestamp: Instant, userId: UserId, unitId: UnitId, amountDeposited: Long) extends UnitEvent