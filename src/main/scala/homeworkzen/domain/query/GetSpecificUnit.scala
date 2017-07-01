package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message._
import homeworkzen.model._

import scala.concurrent.Future

object GetSpecificUnit {

  def apply(userId: UserId, unitId: UnitId)(implicit actorSystem: ActorSystem,
                                            actorMaterializer: ActorMaterializer,
                                            journalReader: JournalReader): Future[Option[UnitInfo]] = {
    val source = journalReader.currentEventsByTag(s"${unitId.id}")
    source.map(_.event)
      .collect {
        case created: UnitCreatedEvent => created
        case withdraw: WithdrawEvent => withdraw
        case deposit: DepositEvent => deposit
      }
      .filter(_.userId == userId)
      .runFold(None: Option[UnitInfo])((current, event) =>
        event match {
          case created: UnitCreatedEvent =>
            Some(UnitInfo(created.unitId, created.unitType, created.maximumCapacity, 0))
          case withdraw: WithdrawEvent =>
            val value = current.get
            Some(value.copy(currentAmount = value.currentAmount - withdraw.amountWithdrawn))
          case deposit: DepositEvent =>
            val value = current.get
            Some(value.copy(currentAmount = value.currentAmount + deposit.amountDeposited))
        }
      )
  }

}
