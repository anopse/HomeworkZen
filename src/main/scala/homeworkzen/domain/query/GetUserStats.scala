package homeworkzen.domain.query


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import homeworkzen.model._

import scala.concurrent.Future

object GetUserStats {
  def apply(userId: UserId)(implicit actorSystem: ActorSystem,
                            actorMaterializer: ActorMaterializer): Future[UserStats] = {
    GetAllUnitsStats(userId).map(values => {
      val byType = values.groupBy(_.unitType)
        .mapValues(values => {
          val totalConsumed = values.map(_.totalConsumed).sum
          val totalGenerated = values.map(_.totalGenerated).sum
          GroupedStats(totalConsumed, totalGenerated, values.length)
        })
      val globalTotalConsumed = byType.values.map(_.totalConsumed).sum
      val globalTotalGenerated = byType.values.map(_.totalGenerated).sum
      val global = GroupedStats(globalTotalConsumed, globalTotalGenerated, values.length)
      UserStats(global, byType, values)
    })(actorSystem.dispatcher)
  }
}