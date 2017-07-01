package homeworkzen.rest.dto.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.model.UnitStats
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class UnitStatsDTO(id: String,
                        stationType: String,
                        totalConsumed: Long,
                        totalGenerated: Long)
object UnitStatsDTO extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val jsonFormat: RootJsonFormat[UnitStatsDTO] = jsonFormat4(UnitStatsDTO.apply)

  def fromUnitStats(unitStats: UnitStats) =
    UnitStatsDTO(unitStats.unitId.id.toString,
      unitStats.unitType.id,
      unitStats.totalConsumed,
      unitStats.totalGenerated)
}
