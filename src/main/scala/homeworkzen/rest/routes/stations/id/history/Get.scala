package homeworkzen.rest.routes.stations.id.history

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import homeworkzen.domain.query.GetUnitHistory
import homeworkzen.model.{UnitId, UserEntry}
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.rest.dto.TimeStampedValueDTO
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type
import scala.util.{Failure, Success}

object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("stations" / JavaUUID / "history") { stationId =>
      get {
        asAuthentified { entry: UserEntry =>
          val unitId = UnitId(stationId)
          val query = GetUnitHistory(entry.id, unitId)(context.system, context.materializer)
          onComplete(query) {
            case Success(seq) =>
              val values = seq.map(data => TimeStampedValueDTO(data._1, data._2)).toList
              if (values.isEmpty)
                ResponseBuilder.notFound(unitId)
              else
                ResponseBuilder.successHistory(StatusCodes.OK, values)
            case Failure(_) => ResponseBuilder.internalServerError()
          }
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}