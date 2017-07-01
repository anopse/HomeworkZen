package homeworkzen.rest.dto.utils

import java.time.Instant

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

object NativeTypeJSONFormats {
  implicit val instantFormat = new RootJsonFormat[Instant] {
    override def write(obj: Instant) = JsString(obj.toString)

    override def read(json: JsValue): Instant = json match {
      case JsString(str) => Instant.parse(str)
      case _ => throw DeserializationException("Can't deserialize instant")
    }
  }
}
