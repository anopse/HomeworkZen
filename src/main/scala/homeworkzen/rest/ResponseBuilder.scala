package homeworkzen.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object ResponseBuilder extends DefaultJsonProtocol with SprayJsonSupport {

  def success(statusCode: StatusCode): StandardRoute =
    complete(statusCode -> ResponseTemplate(statusCode.intValue, "success"))

  private implicit val format: RootJsonFormat[ResponseTemplate] = jsonFormat2(ResponseTemplate)

  def failure(statusCode: StatusCode, message: String): StandardRoute =
    complete(statusCode -> ResponseTemplate(statusCode.intValue, message))

  private case class ResponseTemplate(statusCode: Int, message: String)

}
