package homeworkzen.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.rest.dto._
import spray.json._

object DTOJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val registerUserRequestFormat: RootJsonFormat[RegisterUserRequestDTO] = jsonFormat2(RegisterUserRequestDTO)
}
