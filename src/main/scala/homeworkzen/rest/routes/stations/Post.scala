package homeworkzen.rest.routes.stations

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import homeworkzen.Config
import homeworkzen.domain.command.message._
import homeworkzen.model.{UnitType, UserEntry, UserId}
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.util.TypeHelper
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.reflect.runtime.universe.Type

object Post extends RestRoute with DefaultJsonProtocol with SprayJsonSupport {
  override def route(implicit context: RestContext): Route =
    path("stations") {
      post {
        entity(as[Request]) { request =>
          asAuthentified { userEntry: UserEntry =>
            request.toCommand(userEntry.id) match {
              case None => ResponseBuilder.failure(StatusCodes.BadRequest, "unitType parameter is incorrect")
              case Some(command) => val result = (context.userManager ? command) (Config.Api.askTimeout).mapTo[CreateUnitResult]
                onSuccess(result) {
                  case CreateUnitResult(_, Right(unitId)) =>
                    ResponseBuilder.success(StatusCodes.Created, unitId.id)
                  case CreateUnitResult(_, Left(InvalidMaximumCapacityValue)) =>
                    ResponseBuilder.failure(StatusCodes.BadRequest, "maximumCapacity parameter is incorrect")
                  case CreateUnitResult(_, Left(CreateUnitUserNotFound)) =>
                    ResponseBuilder.internalServerError()
                }
            }

          }
        }
      }
    }

  private implicit val format: RootJsonFormat[Request] = jsonFormat2(Request)

  override def thisType: Type = TypeHelper.getType(this)

  private case class Request(maximumCapacity: Long, unitType: String) {
    def toCommand(userId: UserId): Option[CreateUnitCommand] =
      UnitType.fromId(unitType)
        .map(CreateUnitCommand(userId, maximumCapacity, _))
  }

}