package homeworkzen.model

sealed case class UnitType(id: String)

object UnitType {
  val Eolian = UnitType("eolian")
  val Solar = UnitType("solar")
  val Thermal = UnitType("thermal")

  val knownTypes: List[UnitType] = List(Eolian, Solar, Thermal)

  def fromId(id: String): Option[UnitType] = knownTypes.find(_.id == id)
}

