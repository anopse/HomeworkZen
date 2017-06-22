package homeworkzen.domain.query

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message.{DepositEvent, UnitCreatedEvent, WithdrawEvent}
import homeworkzen.model.{UnitInfo, UserId}

import scala.concurrent.Future


object GetAllUnits {

  def apply(user: UserId)(implicit actorSystem: ActorSystem,
                          actorMaterializer: ActorMaterializer): Future[Seq[UnitInfo]] = {
    val queries = PersistenceQuery(actorSystem).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
    val source = queries.currentEventsByTag(s"${user.id}")
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
