package homeworkzen.rest.routes.stations.id.deposit

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import homeworkzen.Config
import homeworkzen.domain.command.message._
import homeworkzen.model.{UnitId, UserEntry, UserId}
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.util.TypeHelper
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.reflect.runtime.universe.Type

object Post extends RestRoute with DefaultJsonProtocol with SprayJsonSupport {
  override def route(implicit context: RestContext): Route =
    path("stations" / JavaUUID / "deposit") { stationId =>
      post {
        entity(as[Request]) { request =>
          asAuthentified { userEntry: UserEntry =>
            val unitId = UnitId(stationId)
            val command = request.toCommand(userEntry.id, unitId)
            val result = (context.userCluster ? command) (Config.Api.askTimeout).mapTo[DepositResult]
            onSuccess(result) {
              case DepositResult(_, Right(newAmount)) =>
                ResponseBuilder.successNewAmount(newAmount)
              case DepositResult(_, Left(InvalidDepositAmount)) =>
                ResponseBuilder.failure(StatusCodes.BadRequest, "amount parameter is invalid")
              case DepositResult(_, Left(DepositExceedCapacity)) =>
                ResponseBuilder.failure(StatusCodes.Conflict, "deposit would exceeds station maximum capacity")
              case DepositResult(_, Left(DepositUnitNotFound)) =>
                ResponseBuilder.notFound(unitId)
              case DepositResult(_, Left(DepositUserNotFound)) =>
                ResponseBuilder.internalServerError()
            }
          }
        }
      }
    }

  private implicit val format: RootJsonFormat[Request] = jsonFormat1(Request)

  override def thisType: Type = TypeHelper.getType(this)

  private case class Request(amount: Long) {
    def toCommand(userId: UserId, unitId: UnitId): DepositCommand =
      DepositCommand(userId, unitId, amount)
  }

}