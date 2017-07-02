package homeworkzen.domain.utils

import java.time.Instant

import akka.NotUsed
import akka.persistence.query.EventEnvelope
import akka.stream.scaladsl.Source
import homeworkzen.domain.command.message.Event

import scala.collection.immutable

object SourceBuilder {
  def sourceFromSeq(events: Seq[Event]): Source[EventEnvelope, NotUsed] = {
    val envelopes = events.map(event => {
      EventEnvelope(null, "", 0, event)
    })
    Source(envelopes.to[immutable.Seq])
  }
}
