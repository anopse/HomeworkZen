package homeworkzen.domain.command.message

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

// todo : document somehow that Long result is the new amount available
case class DepositResult(request: DepositCommand, result: Either[DepositError, Long])

case class DepositEvent(userId: UserId, unitId: UnitId, amountDeposited: Long) extends UnitEvent