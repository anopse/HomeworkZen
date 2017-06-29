package homeworkzen.rest.dto

import homeworkzen.model.UnitStats

case class UnitStatsDTO(id: String,
                        stationType: String,
                        totalConsumed: Long,
                        totalGenerated: Long)
object UnitStatsDTO {
  def fromUnitStats(unitStats: UnitStats) =
    UnitStatsDTO(unitStats.unitId.id.toString,
      unitStats.unitType.id,
      unitStats.totalConsumed,
      unitStats.totalGenerated)
}
