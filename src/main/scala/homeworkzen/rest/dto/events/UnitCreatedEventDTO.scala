package homeworkzen.rest.dto.events

import java.time.Instant

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.domain.command.message.UnitCreatedEvent
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class UnitCreatedEventDTO(time: Instant,
                               stationId: String,
                               maximumCapacity: Long,
                               stationType: String) extends UnitEventDTO {
  override def eventId: String = "UnitCreatedEvent"
}

object UnitCreatedEventDTO extends DefaultJsonProtocol with SprayJsonSupport {

  import homeworkzen.rest.dto.utils.NativeTypeJSONFormats._

  val jsonFormat: RootJsonFormat[UnitCreatedEventDTO] = jsonFormat4(UnitCreatedEventDTO.apply)

  def fromUnitCreatedEvent(unitCreatedEvent: UnitCreatedEvent): UnitCreatedEventDTO =
    UnitCreatedEventDTO(unitCreatedEvent.timestamp,
      unitCreatedEvent.unitId.id.toString,
      unitCreatedEvent.maximumCapacity,
      unitCreatedEvent.unitType.id)
}