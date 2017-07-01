package homeworkzen.rest.dto.events

import homeworkzen.domain.command.message._
import spray.json._
import DefaultJsonProtocol._

object EventMapper {

  val modelEventToDTO: PartialFunction[UserEvent, EventDTO] = {
    case deposited: DepositEvent => DepositedEventDTO.fromDepositEvent(deposited)
    case withdrawn: WithdrawEvent => WithdrawnEventDTO.fromWithdrawEvent(withdrawn)
    case unitCreated: UnitCreatedEvent => UnitCreatedEventDTO.fromUnitCreatedEvent(unitCreated)
  }

  def dtoToJson: PartialFunction[EventDTO, String] = {
    case deposited: DepositedEventDTO =>
      implicit val format = DepositedEventDTO.jsonFormat
      Map(deposited.eventId -> deposited).toJson.compactPrint
    case withdrawn: WithdrawnEventDTO =>
      implicit val format = WithdrawnEventDTO.jsonFormat
      Map(withdrawn.eventId -> withdrawn).toJson.compactPrint
    case unitCreated: UnitCreatedEventDTO =>
      implicit val format = UnitCreatedEventDTO.jsonFormat
      Map(unitCreated.eventId -> unitCreated).toJson.compactPrint
  }
}
