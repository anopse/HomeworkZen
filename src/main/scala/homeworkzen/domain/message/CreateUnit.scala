package homeworkzen.domain.message

import homeworkzen.model._

sealed trait CreateUnitError

object InvalidMaximumCapacityValue extends CreateUnitError

case class CreateUnitCommand(user: UserId, maximumCapacity: Long, unitType: UnitType)

case class CreateUnitResult(request: CreateUnitCommand, result: Either[CreateUnitError, UnitId])

case class UnitCreatedEvent(userId: UserId, unitId: UnitId, maximumCapacity: Long, unitType: UnitType)