package homeworkzen.rest.dto.events

import java.time.Instant

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.domain.command.message.WithdrawEvent
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class WithdrawnEventDTO(time: Instant, stationId: String, amountWithdrawn: Long) extends UnitEventDTO {
  override def eventId: String = "WithdrawnEvent"
}

object WithdrawnEventDTO extends DefaultJsonProtocol with SprayJsonSupport {

  import homeworkzen.rest.dto.utils.NativeTypeJSONFormats._

  val jsonFormat: RootJsonFormat[WithdrawnEventDTO] = jsonFormat3(WithdrawnEventDTO.apply)

  def fromWithdrawEvent(withdrawEvent: WithdrawEvent): WithdrawnEventDTO =
    WithdrawnEventDTO(withdrawEvent.timestamp, withdrawEvent.unitId.id.toString, withdrawEvent.amountWithdrawn)
}