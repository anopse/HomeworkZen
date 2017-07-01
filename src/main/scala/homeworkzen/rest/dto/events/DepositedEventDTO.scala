package homeworkzen.rest.dto.events

import java.time.Instant

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.domain.command.message.DepositEvent
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class DepositedEventDTO(time: Instant, stationId: String, amountDeposited: Long) extends UnitEventDTO {
  override def eventId: String = "DepositedEvent"
}

object DepositedEventDTO extends DefaultJsonProtocol with SprayJsonSupport {

  import homeworkzen.rest.dto.utils.NativeTypeJSONFormats._

  val jsonFormat: RootJsonFormat[DepositedEventDTO] = jsonFormat3(DepositedEventDTO.apply)

  def fromDepositEvent(depositEvent: DepositEvent): DepositedEventDTO =
    DepositedEventDTO(depositEvent.timestamp, depositEvent.unitId.id.toString, depositEvent.amountDeposited)
}