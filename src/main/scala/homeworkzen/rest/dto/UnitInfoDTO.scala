package homeworkzen.rest.dto

import homeworkzen.model.UnitInfo

case class UnitInfoDTO(id: String,
                       stationType: String,
                       maximumCapacity: Long,
                       currentAmount: Long)

object UnitInfoDTO {
  def fromUnitInfo(unitInfo: UnitInfo) =
    UnitInfoDTO(unitInfo.unitId.id.toString,
      unitInfo.unitType.id,
      unitInfo.maximumCapacity,
      unitInfo.currentAmount)
}
