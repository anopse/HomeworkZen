package homeworkzen.rest.dto.model

import java.time.Instant

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class TimeStampedValueDTO(time: Instant,
                               amount: Long)

object TimeStampedValueDTO extends DefaultJsonProtocol with SprayJsonSupport {
  import homeworkzen.rest.dto.utils.NativeTypeJSONFormats._

  implicit val jsonFormat: RootJsonFormat[TimeStampedValueDTO] = jsonFormat2(TimeStampedValueDTO.apply)
}