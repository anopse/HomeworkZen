package homeworkzen.model

case class GroupedStats(totalConsumed: Long, totalGenerated: Long, count: Long)

case class UserStats(global: GroupedStats, byUnitType: Map[UnitType, GroupedStats], individualStats: Seq[UnitStats])