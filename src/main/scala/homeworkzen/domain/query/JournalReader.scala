package homeworkzen.domain.query

import akka.NotUsed
import akka.persistence.query.EventEnvelope
import akka.stream.scaladsl.Source

trait JournalReader {
  def currentEventsByTag(tag: String): Source[EventEnvelope, NotUsed]

  def eventsByTag(tag: String): Source[EventEnvelope, NotUsed]

  def newEventsByTag(tag: String): Source[EventEnvelope, NotUsed]
}
