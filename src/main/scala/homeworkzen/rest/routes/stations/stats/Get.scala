package homeworkzen.rest.routes.stations.stats

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import homeworkzen.domain.query.GetUserStats
import homeworkzen.model.UserEntry
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type
import scala.util.{Failure, Success}

object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("stations" / "stats") {
      get {
        asAuthentified { userEntry: UserEntry =>
          import context._
          val query = GetUserStats(userEntry.id)
          onComplete(query) {
            case Success(infos) =>
              import homeworkzen.rest.dto.model.UserStatsDTO._
              ResponseBuilder.success(StatusCodes.OK, fromUserStats(infos))
            case Failure(_) => ResponseBuilder.internalServerError()
          }
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}