package homeworkzen.rest.routes.stations

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import homeworkzen.domain.query.GetAllUnitsId
import homeworkzen.model.UserEntry
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type
import scala.util.{Failure, Success}

object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("stations") {
      get {
        asAuthentified { userEntry: UserEntry =>
          val query = GetAllUnitsId(userEntry.id)(context.system, context.materializer)
            .map(_.map(_.id))(context.system.dispatcher)
          onComplete(query) {
            case Success(ids) => ResponseBuilder.success(StatusCodes.OK, ids.toList)
            case Failure(_) => ResponseBuilder.internalServerError()
          }
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}