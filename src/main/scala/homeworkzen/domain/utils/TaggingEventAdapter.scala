package homeworkzen.domain.utils

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import homeworkzen.domain.command.message.{UnitEvent, UserCreatedEvent, UserEvent}
import homeworkzen.util.Base64

class TaggingEventAdapter extends WriteEventAdapter {
  override def toJournal(event: Any): Any = event match {
    case unitEvent: UnitEvent => Tagged(unitEvent, Set(unitEvent.userId.id.toString, unitEvent.unitId.id.toString))
    case createdEvent: UserCreatedEvent => // add username to allow authentification query
      Tagged(createdEvent, Set(createdEvent.userId.id.toString, Base64.encodeString(createdEvent.userEntry.username)))
    case userEvent: UserEvent => Tagged(userEvent, Set(userEvent.userId.id.toString))
    case _ => event
  }

  override def manifest(event: Any): String = ""
}
