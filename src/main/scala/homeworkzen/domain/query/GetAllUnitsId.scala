package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.message.UnitCreatedEvent
import homeworkzen.model.{UnitId, UserId}

import scala.concurrent.Future

object GetAllUnitsId {
  def apply(user: UserId)(implicit actorSystem: ActorSystem, actorMaterializer: ActorMaterializer): Future[Seq[UnitId]] = {
    val persistenceId = s"userworker-${user.id}"
    val tag = s"${user.id}"
    val queries = PersistenceQuery(actorSystem).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
    val source = queries.currentEventsByTag(tag)
    source.map(_.event)
      .collect { case e: UnitCreatedEvent => e }
      .runFold(List.empty[UnitId])((list, event) => event.unitId :: list)
  }
}
