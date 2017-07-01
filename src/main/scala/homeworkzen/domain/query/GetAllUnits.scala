package homeworkzen.domain.query

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message._
import homeworkzen.model._

import scala.concurrent.Future


object GetAllUnits {

  def apply(user: UserId)(implicit actorSystem: ActorSystem,
                          actorMaterializer: ActorMaterializer,
                          journalReader: JournalReader): Future[Seq[UnitInfo]] = {
    val source = journalReader.currentEventsByTag(s"${user.id}")
    source.map(_.event)
      .collect {
        case created: UnitCreatedEvent => created
        case withdraw: WithdrawEvent => withdraw
        case deposit: DepositEvent => deposit
      }
      .runFold(Map.empty: Map[UUID, UnitInfo])((map, event) =>
        event match {
          case created: UnitCreatedEvent => map +
            (created.unitId.id -> UnitInfo(created.unitId, created.unitType, created.maximumCapacity, 0))
          case withdraw: WithdrawEvent => map +
            (withdraw.unitId.id -> {
              val value = map(withdraw.unitId.id)
              value.copy(currentAmount = value.currentAmount - withdraw.amountWithdrawn)
            })
          case deposit: DepositEvent => map +
            (deposit.unitId.id -> {
              val value = map(deposit.unitId.id)
              value.copy(currentAmount = value.currentAmount + deposit.amountDeposited)
            })
        }).map(_.values.toSeq)(actorSystem.dispatcher)
  }

}



