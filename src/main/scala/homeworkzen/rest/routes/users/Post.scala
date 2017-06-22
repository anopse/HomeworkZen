package homeworkzen.rest.routes.users

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, path, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import homeworkzen.Config
import homeworkzen.domain.message.{CreateUserCommand, CreateUserResult, UsernameAlreadyExist}
import homeworkzen.rest.{ResponseBuilder, RestContext, RestRoute}
import homeworkzen.util.{Hasher, TypeHelper}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.reflect.runtime.universe.Type


// todo : make swagger not use return value as model then enable it again
//import io.swagger.annotations._
//import javax.ws.rs.Path

//@Api(value = "/users", consumes = "application/json", produces = "application/json")
//@Path("/users")
object Post extends RestRoute with DefaultJsonProtocol with SprayJsonSupport {

  //  @ApiOperation(value = "Create a new user account", nickname = "anonymousHello", httpMethod = "POST")
  //  @ApiResponses(Array(
  //    new ApiResponse(code = 201, message = "Successfully created an user."),
  //    new ApiResponse(code = 409, message = "Creation failed, Username is already in used.")
  //  ))
  //  @ApiImplicitParams(Array(
  //    new ApiImplicitParam(name = "body",
  //      required = true,
  //      example = "{username=\"toto\", password=\"secret\"}",
  //      allowMultiple = false,
  //      `type` = "string",
  //      paramType = "body"
  //    )))
  //  def doc = Unit

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

  override def thisType: Type = TypeHelper.getType(this)

  private case class Request(username: String, password: String) {
    def toCommand: CreateUserCommand = CreateUserCommand(username, Hasher(password))
  }

}
