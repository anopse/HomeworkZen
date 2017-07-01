package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message._
import homeworkzen.model._

import scala.concurrent.Future

object GetUnitStats {

  def apply(userId: UserId, unitId: UnitId)(implicit actorSystem: ActorSystem,
                                            actorMaterializer: ActorMaterializer,
                                            journalReader: JournalReader): Future[Option[UnitStats]] = {
    val source = journalReader.currentEventsByTag(s"${unitId.id}")
    source.map(_.event)
      .collect {
        case created: UnitCreatedEvent => created
        case withdraw: WithdrawEvent => withdraw
        case deposit: DepositEvent => deposit
      }
      .filter(_.userId == userId)
      .runFold(None: Option[UnitStats])((current, event) =>
        event match {
          case created: UnitCreatedEvent =>
            Some(UnitStats(created.unitId, created.unitType, 0, 0))
          case withdraw: WithdrawEvent =>
            val value = current.get
            Some(value.copy(totalConsumed = value.totalConsumed + withdraw.amountWithdrawn))
          case deposit: DepositEvent =>
            val value = current.get
            Some(value.copy(totalGenerated = value.totalGenerated + deposit.amountDeposited))
        }
      )
  }

}
