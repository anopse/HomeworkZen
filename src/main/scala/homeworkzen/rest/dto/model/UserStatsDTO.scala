package homeworkzen.rest.dto.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import homeworkzen.model.{GroupedStats, UserStats}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class GroupedStatsDTO(count: Long, totalConsumed: Long, totalGenerated: Long)

object GroupedStatsDTO extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val jsonFormat: RootJsonFormat[GroupedStatsDTO] = jsonFormat3(GroupedStatsDTO.apply)

  def fromGroupedStats(groupedStats: GroupedStats): GroupedStatsDTO =
    GroupedStatsDTO(groupedStats.count, groupedStats.totalConsumed, groupedStats.totalGenerated)
}

case class UserStatsDTO(global: GroupedStatsDTO,
                        byStationType: Map[String, GroupedStatsDTO],
                        individualStats: List[UnitStatsDTO])

object UserStatsDTO extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val jsonFormat: RootJsonFormat[UserStatsDTO] = jsonFormat3(UserStatsDTO.apply)

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
