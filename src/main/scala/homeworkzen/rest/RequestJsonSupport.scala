package homeworkzen.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.auth.message.UserRegistrationRequest
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object RequestJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val UserRegistrationRequestFormat: RootJsonFormat[UserRegistrationRequest] = jsonFormat2(UserRegistrationRequest)
}
