package homeworkzen.domain.utils

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.{EventEnvelope2, PersistenceQuery}
import akka.stream.scaladsl.Source

object QueryHelper {
  def currentEventsByTag(tag: String)(implicit actorSystem: ActorSystem): Source[EventEnvelope2, NotUsed] =
    journal.currentEventsByTag(tag)

  private def journal(implicit actorSystem: ActorSystem) =
    PersistenceQuery(actorSystem).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
}
