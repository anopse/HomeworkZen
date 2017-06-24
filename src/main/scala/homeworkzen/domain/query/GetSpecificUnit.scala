package homeworkzen.domain.query

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message._
import homeworkzen.domain.utils.QueryHelper
import homeworkzen.model._

import scala.concurrent.Future

object GetSpecificUnit {

  def apply(user: UserId, unitId: UnitId)(implicit actorSystem: ActorSystem,
                                          actorMaterializer: ActorMaterializer): Future[Option[UnitInfo]] = {
    val source = QueryHelper.currentEventsByTag(s"${user.id}")
    source.map(_.event)
      .collect {
        case created: UnitCreatedEvent => created
        case withdraw: WithdrawEvent => withdraw
        case deposit: DepositEvent => deposit
      }
      .runFold(Map.empty: Map[UUID, UnitInfo])((map, event) =>
        event match {
          case event: UnitEvent if event.unitId != unitId => map // ignore event of other units
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
        })
      .map(infosMap => infosMap.values.toList match {
        case value :: Nil => Some(value)
        case _ => None
      })(actorSystem.dispatcher)
  }
}
