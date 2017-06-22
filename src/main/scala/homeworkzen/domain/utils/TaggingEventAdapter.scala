package homeworkzen.domain.utils

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import homeworkzen.domain.command.message.UnitEvent

class TaggingEventAdapter extends WriteEventAdapter {
  override def toJournal(event: Any): Any = event match {
    case unitEvent: UnitEvent => Tagged(unitEvent, Set(unitEvent.userId.id.toString))
    case _ => event
  }

  override def manifest(event: Any): String = ""
}
