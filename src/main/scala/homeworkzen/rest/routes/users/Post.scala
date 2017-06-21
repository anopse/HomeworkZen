package homeworkzen.rest.routes.users

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, path, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import homeworkzen.Config
import homeworkzen.domain.message.{CreateUserCommand, CreateUserResult, UsernameAlreadyExist}
import homeworkzen.rest.{ResponseBuilder, RestContext, RestRoute}
import homeworkzen.util.Hasher
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object Post extends RestRoute with DefaultJsonProtocol with SprayJsonSupport {

  override def route(implicit context: RestContext): Route =
    path("users") {
      post {
        entity(as[Request]) { request =>
          val auth = (context.userManager ? request.toCommand) (Config.Api.authTimeout).mapTo[CreateUserResult]
          onSuccess(auth) {
            case CreateUserResult(_, Right(_)) =>
              ResponseBuilder.success(StatusCodes.Created)
            case CreateUserResult(_, Left(UsernameAlreadyExist)) =>
              ResponseBuilder.failure(StatusCodes.Conflict, "username is already in used")
          }
        }
      }
    }

  private implicit val format: RootJsonFormat[Request] = jsonFormat2(Request)

  private case class Request(username: String, password: String) {
    def toCommand: CreateUserCommand = CreateUserCommand(username, Hasher(password))
  }

}
