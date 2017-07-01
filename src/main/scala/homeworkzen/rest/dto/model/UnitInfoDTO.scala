package homeworkzen.rest.dto.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.model.UnitInfo
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class UnitInfoDTO(id: String,
                       stationType: String,
                       maximumCapacity: Long,
                       currentAmount: Long)

object UnitInfoDTO extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val jsonFormat: RootJsonFormat[UnitInfoDTO] = jsonFormat4(UnitInfoDTO.apply)

  def fromUnitInfo(unitInfo: UnitInfo) =
    UnitInfoDTO(unitInfo.unitId.id.toString,
      unitInfo.unitType.id,
      unitInfo.maximumCapacity,
      unitInfo.currentAmount)
}
