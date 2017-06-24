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
  def apply(userId: UserId, unitId: UnitId, from: Option[Instant], to: Option[Instant])
           (implicit actorSystem: ActorSystem, actorMaterializer: ActorMaterializer): Future[Seq[(Instant, Long)]] = {
    implicit val executionContext = actorSystem.dispatcher
    val source = QueryHelper.currentEventsByTag(s"${unitId.id}")
    val history = source.map(_.event)
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

    val historyFrom = from match {
      case None => history
      case Some(fromDate) =>
        val indexedHistory = history.map(_.toIndexedSeq)
        indexedHistory.map { values =>
          val fromIdx = values.indexWhere(_._1.compareTo(fromDate) > 0)
          if (fromIdx == 0) values
          else {
            val tail = values.drop(fromIdx)
            val head = (fromDate, values(fromIdx - 1)._2)
            head +: tail
          }
        }
    }

    val historyFromTo = to match {
      case None => historyFrom
      case Some(toDate) => historyFrom.map(_.takeWhile(_._1.compareTo(toDate) < 0))
    }
    historyFromTo
  }

}
