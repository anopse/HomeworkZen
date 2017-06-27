package homeworkzen.rest.routes.stations.id.withdraw

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
    path("stations" / JavaUUID / "withdraw") { stationId =>
      post {
        entity(as[Request]) { request =>
          asAuthentified { userEntry: UserEntry =>
            val unitId = UnitId(stationId)
            val command = request.toCommand(userEntry.id, unitId)
            val result = (context.userCluster ? command) (Config.Api.askTimeout).mapTo[WithdrawResult]
            onSuccess(result) {
              case WithdrawResult(_, Right(newAmount)) =>
                ResponseBuilder.successNewAmount(newAmount)
              case WithdrawResult(_, Left(InvalidWithdrawAmount)) =>
                ResponseBuilder.failure(StatusCodes.BadRequest, "amount parameter is invalid")
              case WithdrawResult(_, Left(WithdrawExceedAvailableAmount)) =>
                ResponseBuilder.failure(StatusCodes.Conflict, "requested withdraw amount exceeds current available amount")
              case WithdrawResult(_, Left(WithdrawUnitNotFound)) =>
                ResponseBuilder.notFound(unitId)
              case WithdrawResult(_, Left(WithdrawUserNotFound)) =>
                ResponseBuilder.internalServerError()
            }
          }
        }
      }
    }

  private implicit val format: RootJsonFormat[Request] = jsonFormat1(Request)

  override def thisType: Type = TypeHelper.getType(this)

  private case class Request(amount: Long) {
    def toCommand(userId: UserId, unitId: UnitId): WithdrawCommand =
      WithdrawCommand(userId, unitId, amount)
  }

}