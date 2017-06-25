package homeworkzen.domain.utils

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.jdbc.query.scaladsl.JdbcReadJournal
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.scaladsl.Source

object QueryHelper {
  def currentEventsByTag(tag: String)(implicit actorSystem: ActorSystem): Source[EventEnvelope, NotUsed] =
    journal.currentEventsByTag(tag, 0)

  private def journal(implicit actorSystem: ActorSystem) =
    PersistenceQuery(actorSystem).readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)
}
