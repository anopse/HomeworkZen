package homeworkzen.rest.routes.stations.id.stats

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import homeworkzen.domain.query.GetUnitStats
import homeworkzen.model.{UnitId, UserEntry}
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type
import scala.util.{Failure, Success, Try}

object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("stations" / JavaUUID / "stats") { stationId =>
      get {
        asAuthentified { entry: UserEntry =>
          import context._
          val unitId = UnitId(stationId)
          val query = GetUnitStats(entry.id, unitId)
          onComplete(query) {
            case Success(Some(info)) =>
              import homeworkzen.rest.dto.model.UnitStatsDTO._
              ResponseBuilder.success(StatusCodes.OK, fromUnitStats(info))
            case Success(None) => ResponseBuilder.notFound(unitId)
            case Failure(_) => ResponseBuilder.internalServerError()
          }
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}