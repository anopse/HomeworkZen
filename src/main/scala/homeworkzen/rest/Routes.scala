package homeworkzen.rest

import akka.actor._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import akka.http.scaladsl.{Http, server}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import homeworkzen.Config
import homeworkzen.domain.message._
import homeworkzen.model.UserEntry
import homeworkzen.rest.DTOJsonSupport._
import homeworkzen.rest.dto.RegisterUserRequestDTO
import homeworkzen.util.Hasher

import scala.concurrent.Future

object Routes {

  def bindRoutes(authManager: ActorRef)(implicit system: ActorSystem, materializer: ActorMaterializer): Future[Http.ServerBinding] =
    Http().bindAndHandle(routes(authManager), Config.Api.interface, Config.Api.port)

  private def routes(userManager: ActorRef)(implicit system: ActorSystem): server.Route =
    path("register") {
      post {
        entity(as[RegisterUserRequestDTO]) { request =>
          val auth = (userManager ? request.toCommand) (Config.Api.authTimeout).mapTo[CreateUserResult]
          onSuccess(auth) {
            case CreateUserResult(_, Right(id)) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"success ${request.username} -> $id"))
            case CreateUserResult(_, Left(e)) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"failure ${request.username} -> $e"))
          }
        }
      }
    } ~ path("testAuth") {
      get {
        def myUserPassAuthenticator(credentials: Credentials): Future[Option[UserEntry]] =
          credentials match {
            case p@Provided(username) =>
              val result = (userManager ? GetUserEntryRequest(username)) (Config.Api.authTimeout).mapTo[GetUserEntryResult]
              import system.dispatcher
              result.map {
                case GetUserEntryResult(_, Left(_)) => None
                case GetUserEntryResult(_, Right(entry)) =>
                  if (p.verify(entry.secret, Hasher(_)))
                    Some(entry)
                  else
                    None
              }
            case _ => Future.successful(None)
          }

        // false compile error from intellij idea
        authenticateBasicAsync(Config.Api.authRealm, myUserPassAuthenticator) { entry =>
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Hello ${entry.username} </h1>"))
        }
      }
    }

}
