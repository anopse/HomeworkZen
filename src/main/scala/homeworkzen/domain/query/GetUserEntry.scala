package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message._
import homeworkzen.model._
import homeworkzen.util.Base64

import scala.concurrent.Future

object GetUserEntry {

  def apply(username: String)(implicit actorSystem: ActorSystem,
                              actorMaterializer: ActorMaterializer,
                              journalReader: JournalReader): Future[Option[UserEntry]] = {
    val source = journalReader.currentEventsByTag(Base64.encodeString(username))
    val results = source.map(_.event)
      .collect { case created: UserCreatedEvent => created }
      .filter(_.userEntry.username == username)
      .runFold(Nil: List[UserEntry])((result, event) => event.userEntry :: result)
    results.map{_.headOption}(actorSystem.dispatcher)
  }

}