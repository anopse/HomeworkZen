package homeworkzen.rest.routes.stations.id

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import homeworkzen.domain.query.GetSpecificUnit
import homeworkzen.model.{UnitId, UserEntry}
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type
import scala.util.{Failure, Success}

object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("stations" / JavaUUID) { stationId =>
      get {
        asAuthentified { entry: UserEntry =>
          val unitId = UnitId(stationId)
          val query = GetSpecificUnit(entry.id, unitId)(context.system, context.materializer)
          onComplete(query) {
            case Success(Some(info)) =>
              import homeworkzen.rest.dto.model.UnitInfoDTO._
              ResponseBuilder.success(StatusCodes.OK, fromUnitInfo(info))
            case Success(None) => ResponseBuilder.notFound(unitId)
            case Failure(_) => ResponseBuilder.internalServerError()
          }
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}