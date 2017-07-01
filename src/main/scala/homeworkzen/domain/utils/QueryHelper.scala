package homeworkzen.domain.utils

import java.time.Instant

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.jdbc.query.scaladsl.JdbcReadJournal
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.scaladsl.Source
import homeworkzen.domain.command.message.Event

import scala.concurrent.Future

object QueryHelper {
  def currentEventsByTag(tag: String)(implicit actorSystem: ActorSystem): Source[EventEnvelope, NotUsed] =
    journal.currentEventsByTag(tag, 0)

  def eventsByTag(tag: String)(implicit actorSystem: ActorSystem): Source[EventEnvelope, NotUsed] =
    journal.eventsByTag(tag, 0)

  private def tryGetEventTime(event: Any): Option[Instant] = event match {
    case e: Event => Some(e.timestamp)
    case _ => None
  }

  def newEventsByTag(tag: String)(implicit actorSystem: ActorSystem): Source[EventEnvelope, NotUsed] = {
    val now = Instant.now()
    val isNotNew = (envelope: EventEnvelope) => !tryGetEventTime(envelope.event).exists(now.compareTo(_) < 0)
    eventsByTag(tag).dropWhile(isNotNew)
  }

  private def journal(implicit actorSystem: ActorSystem) =
    PersistenceQuery(actorSystem).readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)
}
