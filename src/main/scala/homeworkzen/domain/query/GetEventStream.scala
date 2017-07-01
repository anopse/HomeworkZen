package homeworkzen.domain.query

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import homeworkzen.domain.command.message._
import homeworkzen.model._

object GetEventStream {
  def apply(userId: UserId)(implicit actorSystem: ActorSystem,
                            journalReader: JournalReader): Source[UserEvent, NotUsed] = {
    journalReader.newEventsByTag(userId.id.toString)
      .map(_.event)
      .collect { case event: UserEvent => event }
      .filter(_.userId == userId)
  }
}
