package homeworkzen.domain.command.actor

import java.time.Instant

import akka.persistence.PersistentActor
import homeworkzen.domain.command.message._
import homeworkzen.model._

sealed class UnitWorker(userId: UserId, unitId: UnitId, maximumCapacity: Long, unitType: UnitType) extends PersistentActor {
  private var currentAmount: Long = 0

  override def receiveRecover: Receive = {
    case deposit: DepositEvent => apply(deposit)
    case withdraw: WithdrawEvent => apply(withdraw)
  }

  private def apply(deposit: DepositEvent): Unit = currentAmount = currentAmount + deposit.amountDeposited

  private def apply(withdraw: WithdrawEvent): Unit = currentAmount = currentAmount - withdraw.amountWithdrawn

  override def receiveCommand: Receive = {
    case deposit: DepositCommand => handle(deposit)
    case withdraw: WithdrawCommand => handle(withdraw)
  }

  private def handle(deposit: DepositCommand): Unit = {
    if (deposit.amountToDeposit <= 0) {
      sender ! DepositResult(deposit, Left(InvalidDepositAmount))
    } else if (currentAmount + deposit.amountToDeposit > maximumCapacity) {
      sender ! DepositResult(deposit, Left(DepositExceedCapacity))
    } else {
      val originalSender = sender
      persist(DepositEvent(Instant.now(), userId, unitId, deposit.amountToDeposit)) { event =>
        apply(event)
        originalSender ! DepositResult(deposit, Right(currentAmount))
      }
    }
  }

  private def handle(withdraw: WithdrawCommand): Unit = {
    if (withdraw.amountToWithdraw <= 0) {
      sender ! WithdrawResult(withdraw, Left(InvalidWithdrawAmount))
    } else if (withdraw.amountToWithdraw > currentAmount) {
      sender ! WithdrawResult(withdraw, Left(WithdrawExceedAvailableAmount))
    } else {
      val originalSender = sender
      persist(WithdrawEvent(Instant.now(), userId, unitId, withdraw.amountToWithdraw)) { event =>
        apply(event)
        originalSender ! WithdrawResult(withdraw, Right(currentAmount))
      }
    }
  }

  override def persistenceId: String = s"unitworker-${unitId.id}"
}