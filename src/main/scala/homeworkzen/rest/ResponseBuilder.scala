package homeworkzen.rest

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import homeworkzen.model.UnitInfo
import homeworkzen.rest.dto.UnitInfoDTO
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

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

  def success(statusCode: StatusCode): StandardRoute =
    complete(statusCode -> ResponseTemplate(statusCode.intValue, "success"))

  def failure(statusCode: StatusCode, message: String): StandardRoute =
    complete(statusCode -> ResponseTemplate(statusCode.intValue, message))

  // same as akka http default
  def internalServerError(): StandardRoute =
    complete(StatusCodes.InternalServerError -> "There was an internal server error.")

  private implicit val responseTemplateFormat: RootJsonFormat[ResponseTemplate] = jsonFormat2(ResponseTemplate)
  private implicit val idValuesResponseTemplateFormat: RootJsonFormat[IdValuesResponseTemplate] = jsonFormat3(IdValuesResponseTemplate)
  private implicit val singleIdResponseTemplateFormat: RootJsonFormat[SingleIdResponseTemplate] = jsonFormat3(SingleIdResponseTemplate)
  private implicit val unitInfoDTOFormat: RootJsonFormat[UnitInfoDTO] = jsonFormat4(UnitInfoDTO(_, _, _, _))
  private implicit val multipleInfoResponseTemplateFormat: RootJsonFormat[MultipleUnitInfoResponseTemplate] = jsonFormat3(MultipleUnitInfoResponseTemplate)


  private case class ResponseTemplate(statusCode: Int, message: String)

  private case class IdValuesResponseTemplate(statusCode: Int, message: String, result: List[String])

  private case class SingleIdResponseTemplate(statusCode: Int, message: String, id: String)

  private case class MultipleUnitInfoResponseTemplate(statusCode: Int, message: String, result: List[UnitInfoDTO])

}
