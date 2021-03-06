package homeworkzen.rest.routes.stations.id.history

import java.time.Instant

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import homeworkzen.domain.query.GetUnitHistory
import homeworkzen.model.{UnitId, UserEntry}
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.rest.dto.model.TimeStampedValueDTO
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type
import scala.util.{Failure, Success, Try}

object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("stations" / JavaUUID / "history") { stationId =>
      get {
        parameter('from.?, 'to.?) { (fromArg, toArg) =>
          Try {
            fromArg.map(Instant.parse)
          } match {
            case Success(from) =>
              Try {
                toArg.map(Instant.parse)
              } match {
                case Success(to) =>
                  asAuthentified { entry: UserEntry =>
                    import context._
                    val unitId = UnitId(stationId)
                    val query = GetUnitHistory(entry.id, unitId, from, to)
                    onComplete(query) {
                      case Success(seq) =>
                        val values = seq.map(data => TimeStampedValueDTO(data._1, data._2)).toList
                        if (values.isEmpty)
                          ResponseBuilder.notFound(unitId)
                        else
                          ResponseBuilder.success(StatusCodes.OK, values)
                      case Failure(_) => ResponseBuilder.internalServerError()
                    }
                  }
                case Failure(_) => ResponseBuilder.failure(StatusCodes.BadRequest, "Invalid 'to' parameter")
              }
            case Failure(_) => ResponseBuilder.failure(StatusCodes.BadRequest, "Invalid 'from' parameter")
          }
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}