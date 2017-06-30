package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message._
import homeworkzen.domain.utils.QueryHelper
import homeworkzen.model._

import scala.concurrent.Future

object GetAllUnitsStats {
  def apply(userId: UserId)(implicit actorSystem: ActorSystem,
                            actorMaterializer: ActorMaterializer): Future[List[UnitStats]] = {
    val source = QueryHelper.currentEventsByTag(s"${userId.id}")
    source.map(_.event)
      .collect {
        case created: UnitCreatedEvent => created
        case withdraw: WithdrawEvent => withdraw
        case deposit: DepositEvent => deposit
      }
      .filter(_.userId == userId)
      .runFold(Map.empty[UnitId, UnitStats])((current, event) =>
        event match {
          case created: UnitCreatedEvent =>
            current + (created.unitId -> UnitStats(created.unitId, created.unitType, 0, 0))
          case withdraw: WithdrawEvent =>
            val value = current(withdraw.unitId)
            current + (withdraw.unitId -> value.copy(totalConsumed = value.totalConsumed + withdraw.amountWithdrawn))
          case deposit: DepositEvent =>
            val value = current(deposit.unitId)
            current + (deposit.unitId -> value.copy(totalGenerated = value.totalGenerated + deposit.amountDeposited))
        }
      )
      .map(_.values.toList)(actorSystem.dispatcher)
  }
}