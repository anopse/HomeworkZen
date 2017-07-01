package homeworkzen.rest.dto.model

import homeworkzen.model.{GroupedStats, UserStats}

case class GroupedStatsDTO(count: Long, totalConsumed: Long, totalGenerated: Long)

object GroupedStatsDTO {
  def fromGroupedStats(groupedStats: GroupedStats): GroupedStatsDTO =
    GroupedStatsDTO(groupedStats.count, groupedStats.totalConsumed, groupedStats.totalGenerated)
}

case class UserStatsDTO(global: GroupedStatsDTO,
                        byStationType: Map[String, GroupedStatsDTO],
                        individualStats: List[UnitStatsDTO])

object UserStatsDTO {
  def fromUserStats(userStats: UserStats): UserStatsDTO = {
    val global = GroupedStatsDTO.fromGroupedStats(userStats.global)
    val byType = userStats.byUnitType
      .toSeq
      .map(kvp => kvp._1.id -> GroupedStatsDTO.fromGroupedStats(kvp._2))
      .toMap
    val individual = userStats.individualStats.map(UnitStatsDTO.fromUnitStats).toList
    UserStatsDTO(global, byType, individual)
  }
}
