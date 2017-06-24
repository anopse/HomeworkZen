package homeworkzen.domain.command.message

import java.time.Instant

import homeworkzen.model._

sealed trait CreateUnitError

object InvalidMaximumCapacityValue extends CreateUnitError

object CreateUnitUserNotFound extends CreateUnitError

case class CreateUnitCommand(userId: UserId, maximumCapacity: Long, unitType: UnitType) extends UserCommand {
  val userForwardFailureMessage = CreateUnitResult(this, Left(CreateUnitUserNotFound))
}

case class CreateUnitResult(request: CreateUnitCommand, result: Either[CreateUnitError, UnitId])

case class UnitCreatedEvent(timestamp: Instant, userId: UserId, unitId: UnitId, maximumCapacity: Long, unitType: UnitType) extends UnitEvent