package homeworkzen.domain.command.actor

import java.time.Instant
import java.util.UUID

import akka.actor.Props
import akka.persistence.PersistentActor
import homeworkzen.domain.command.message._
import homeworkzen.model._

sealed class UserWorker extends PersistentActor {
  override def receiveRecover: Receive = {
    case unitCreated: UnitCreatedEvent => apply(unitCreated)
  }

  private def apply(unitCreated: UnitCreatedEvent): Unit = {
    val props = Props(new UnitWorker(unitCreated.userId, unitCreated.unitId, unitCreated.maximumCapacity, unitCreated.unitType))
    context.actorOf(props, unitCreated.unitId.id.toString)
  }

  override def receiveCommand: Receive = {
    case createUnit: CreateUnitCommand => handle(createUnit)
    case unitCommand: UnitCommand => forward(unitCommand)
  }

  private def handle(createUnit: CreateUnitCommand): Unit = {
    if (createUnit.maximumCapacity <= 0) {
      sender ! CreateUnitResult(createUnit, Left(InvalidMaximumCapacityValue))
    } else {
      val unitId = UnitId(UUID.randomUUID())
      val originalSender = sender
      persist(UnitCreatedEvent(Instant.now(), createUnit.userId, unitId, createUnit.maximumCapacity, createUnit.unitType)) { event =>
        apply(event)
        originalSender ! CreateUnitResult(createUnit, Right(unitId))
      }
    }
  }

  private def forward(unitCommand: UnitCommand): Unit = {
    context.child(unitCommand.unitId.id.toString) match {
      case Some(child) => child forward unitCommand
      case None => sender ! unitCommand.unitForwardFailureMessage
    }
  }

  override def persistenceId: String =  self.path.parent.name + "-" + self.path.name
}
