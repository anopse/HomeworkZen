package homeworkzen.rest

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import homeworkzen.model._
import homeworkzen.rest.dto._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

object ResponseBuilder extends DefaultJsonProtocol with SprayJsonSupport {

  def successId(statusCode: StatusCode, values: Seq[UUID]): StandardRoute = {
    val body = IdValuesResponseTemplate(statusCode.intValue, "success", values.map(_.toString).toList)
    complete(statusCode -> body)
  }

  def successId(statusCode: StatusCode, id: UUID): StandardRoute =
    complete(statusCode -> SingleIdResponseTemplate(statusCode.intValue, "success", id.toString))

  def successUnitInfo(statusCode: StatusCode, values: Seq[UnitInfo]): StandardRoute = {
    val dto = values.map(UnitInfoDTO.fromUnitInfo).toList
    val body = MultipleUnitInfoResponseTemplate(statusCode.intValue, "success", dto)
    complete(statusCode -> body)
  }

  def successUnitInfo(statusCode: StatusCode, value: UnitInfo): StandardRoute = {
    val dto = UnitInfoDTO.fromUnitInfo(value)
    val body = SingleUnitInfoResponseTemplate(statusCode.intValue, "success", dto)
    complete(statusCode -> body)
  }

  def successUnitStats(statusCode: StatusCode, value: UnitStats): StandardRoute = {
    val dto = UnitStatsDTO.fromUnitStats(value)
    val body = UnitStatsResponseTemplate(statusCode.intValue, "success", dto)
    complete(statusCode -> body)
  }

  def successUserStats(statusCode: StatusCode, value: UserStats): StandardRoute = {
    val dto = UserStatsDTO.fromUserStats(value)
    val body = UserStatsResponseTemplate(statusCode.intValue, "success", dto)
    complete(statusCode -> body)
  }

  def successNewAmount(value: Long): StandardRoute = {
    val body = NewAmountResponseTemplate(StatusCodes.OK.intValue, "success", value)
    complete(StatusCodes.OK -> body)
  }

  def successHistory(statusCode: StatusCode, values: List[TimeStampedValueDTO]): StandardRoute = {
    val body = HistoryResponseTemplate(statusCode.intValue, "success", values)
    complete(statusCode -> body)
  }

  def success(statusCode: StatusCode): StandardRoute =
    complete(statusCode -> ResponseTemplate(statusCode.intValue, "success"))

  def failure(statusCode: StatusCode, message: String): StandardRoute =
    complete(statusCode -> ResponseTemplate(statusCode.intValue, message))

  def notFound(unitId: UnitId): StandardRoute = {
    val body = ResponseTemplate(StatusCodes.NotFound.intValue, s"Could not find station with id ${unitId.id.toString}")
    complete(StatusCodes.NotFound -> body)
  }

  // same as akka http default
  def internalServerError(): StandardRoute =
    complete(StatusCodes.InternalServerError -> "There was an internal server error.")


  private implicit val instantFormat = new RootJsonFormat[Instant] {
    override def write(obj: Instant) = JsString(obj.toString)

    override def read(json: JsValue): Instant = json match {
      case JsString(str) => Instant.parse(str)
      case _ => throw DeserializationException("Can't deerialize instant")
    }
  }
  private implicit val responseTemplateFormat: RootJsonFormat[ResponseTemplate] = jsonFormat2(ResponseTemplate)
  private implicit val idValuesResponseTemplateFormat: RootJsonFormat[IdValuesResponseTemplate] = jsonFormat3(IdValuesResponseTemplate)
  private implicit val singleIdResponseTemplateFormat: RootJsonFormat[SingleIdResponseTemplate] = jsonFormat3(SingleIdResponseTemplate)
  private implicit val unitInfoDTOFormat: RootJsonFormat[UnitInfoDTO] = jsonFormat4(UnitInfoDTO.apply)
  private implicit val timeStampedValueDTOFormat: RootJsonFormat[TimeStampedValueDTO] = jsonFormat2(TimeStampedValueDTO.apply)
  private implicit val unitStatsDTOFormat: RootJsonFormat[UnitStatsDTO] = jsonFormat4(UnitStatsDTO.apply)
  private implicit val groupedStatsDTOFormat: RootJsonFormat[GroupedStatsDTO] = jsonFormat3(GroupedStatsDTO.apply)
  private implicit val userStatsDTOFormat: RootJsonFormat[UserStatsDTO] = jsonFormat3(UserStatsDTO.apply)
  private implicit val multipleInfoResponseTemplateFormat: RootJsonFormat[MultipleUnitInfoResponseTemplate] = jsonFormat3(MultipleUnitInfoResponseTemplate)
  private implicit val singleInfoResponseTemplateFormat: RootJsonFormat[SingleUnitInfoResponseTemplate] = jsonFormat3(SingleUnitInfoResponseTemplate)
  private implicit val newAmountResponseTemplateFormat: RootJsonFormat[NewAmountResponseTemplate] = jsonFormat3(NewAmountResponseTemplate)
  private implicit val historyResponseTemplateFormat: RootJsonFormat[HistoryResponseTemplate] = jsonFormat3(HistoryResponseTemplate)
  private implicit val unitStatsResponseTemplateFormat: RootJsonFormat[UnitStatsResponseTemplate] = jsonFormat3(UnitStatsResponseTemplate)
  private implicit val userStatsResponseTemplateFormat: RootJsonFormat[UserStatsResponseTemplate] = jsonFormat3(UserStatsResponseTemplate)

  private case class ResponseTemplate(statusCode: Int, message: String)

  private case class IdValuesResponseTemplate(statusCode: Int, message: String, result: List[String])

  private case class SingleIdResponseTemplate(statusCode: Int, message: String, id: String)

  private case class MultipleUnitInfoResponseTemplate(statusCode: Int, message: String, result: List[UnitInfoDTO])

  private case class SingleUnitInfoResponseTemplate(statusCode: Int, message: String, result: UnitInfoDTO)

  private case class NewAmountResponseTemplate(statusCode: Int, message: String, newAmount: Long)

  private case class HistoryResponseTemplate(statusCode: Int, message: String, result: List[TimeStampedValueDTO])

  private case class UnitStatsResponseTemplate(statusCode: Int, message: String, result: UnitStatsDTO)

  private case class UserStatsResponseTemplate(statusCode: Int, message: String, result: UserStatsDTO)

}
