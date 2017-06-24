package homeworkzen.domain.query

import java.time.Instant

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import homeworkzen.domain.command.message._
import homeworkzen.domain.utils.QueryHelper
import homeworkzen.model._

import scala.concurrent.Future

object GetUnitHistory {

  def apply(userId: UserId, unitId: UnitId)(implicit actorSystem: ActorSystem,
                                            actorMaterializer: ActorMaterializer): Future[Seq[(Instant, Long)]] = {
    val source = QueryHelper.currentEventsByTag(s"${unitId.id}")
    source.map(_.event)
      .collect {
        case created: UnitCreatedEvent => created
        case withdraw: WithdrawEvent => withdraw
        case deposit: DepositEvent => deposit
      }
      .filter(_.userId == userId)
      .scan((Instant.MIN, 0l))((state, event) =>
        event match {
          case created: UnitCreatedEvent => (created.timestamp, 0l)
          case withdraw: WithdrawEvent => (withdraw.timestamp, state._2 - withdraw.amountWithdrawn)
          case deposit: DepositEvent => (deposit.timestamp, state._2 + deposit.amountDeposited)
        })
      .drop(1)
      .runWith(Sink.seq)
  }

}
