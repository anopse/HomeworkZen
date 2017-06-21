package homeworkzen.domain.message

import homeworkzen.model._

sealed trait DepositError

object InvalidDepositAmount extends DepositError

object DepositExceedCapacity extends DepositError

object DepositUserNotFound extends DepositError

object DepositUnitNotFound extends DepositError

case class DepositCommand(userId: UserId, unitId: UnitId, amountToDeposit: Long) extends UnitCommand {
  val unitForwardFailureMessage = DepositUnitNotFound
  val userForwardFailureMessage = DepositUserNotFound
}

// todo : document somehow that Long result is the new amount available
case class DepositResult(request: DepositCommand, result: Either[DepositError, Long])

case class DepositEvent(unitId: UnitId, amountDeposited: Long)