package homeworkzen.rest

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object ResponseBuilder extends DefaultJsonProtocol with SprayJsonSupport {

  def success(statusCode: StatusCode, values: List[UUID]): StandardRoute =
    complete(statusCode -> IdValuesResponseTemplate(statusCode.intValue, "success", values.map(_.toString)))

  def success(statusCode: StatusCode, id: UUID): StandardRoute =
    complete(statusCode -> SingleIdResponseTemplate(statusCode.intValue, "success", id.toString))

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

  private case class ResponseTemplate(statusCode: Int, message: String)

  private case class IdValuesResponseTemplate(statusCode: Int, message: String, values: List[String])

  private case class SingleIdResponseTemplate(statusCode: Int, message: String, id: String)

}
