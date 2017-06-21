package homeworkzen.model

sealed trait UnitType {
  def id: String
}

object UnitType {
  val knownTypes: List[UnitType] = List(EolianType, SolarType, ThermalType)

  def fromId(id: String): Option[UnitType] = knownTypes.find(_.id == id)
}

object EolianType extends UnitType {
  def id = "eolian"
}

object SolarType extends UnitType {
  def id = "solar"
}

object ThermalType extends UnitType {
  def id = "thermal"
}
