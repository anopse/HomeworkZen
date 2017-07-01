package homeworkzen.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import homeworkzen.model._
import spray.json.{DefaultJsonProtocol, JsonFormat, RootJsonFormat}

object ResponseBuilder extends DefaultJsonProtocol with SprayJsonSupport {

  private case class ResponseTemplateWithoutResult(statusCode: Int, message: String)

  private case class ResponseTemplateWithResult[T](statusCode: Int, message: String, result: T)

  private implicit val responseTemplateWithoutResultFormat: RootJsonFormat[ResponseTemplateWithoutResult] =
    jsonFormat2(ResponseTemplateWithoutResult)

  // same as akka http default
  def internalServerError(): StandardRoute =
    complete(StatusCodes.InternalServerError -> "There was an internal server error.")

  def success(statusCode: StatusCode): StandardRoute =
    complete(statusCode -> ResponseTemplateWithoutResult(statusCode.intValue, "success"))

  def success[T](statusCode: StatusCode, result: T)(implicit jsonFormat: JsonFormat[T]): StandardRoute = {
    implicit val format: RootJsonFormat[ResponseTemplateWithResult[T]] = jsonFormat3(ResponseTemplateWithResult[T])
    complete(statusCode -> ResponseTemplateWithResult(statusCode.intValue, "success", result))
  }

  def failure(statusCode: StatusCode, message: String): StandardRoute =
    complete(statusCode -> ResponseTemplateWithoutResult(statusCode.intValue, message))

  def notFound(unitId: UnitId): StandardRoute = {
    val body = ResponseTemplateWithoutResult(StatusCodes.NotFound.intValue, s"Could not find station with id ${unitId.id.toString}")
    complete(StatusCodes.NotFound -> body)
  }
}
