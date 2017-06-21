package homeworkzen.rest

import akka.http.scaladsl.server
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials.Provided
import akka.http.scaladsl.server.directives.SecurityDirectives._
import akka.http.scaladsl.server.directives._
import akka.pattern.ask
import homeworkzen.Config
import homeworkzen.domain.message.{GetUserEntryRequest, GetUserEntryResult}
import homeworkzen.model.UserEntry
import homeworkzen.util.Hasher

import scala.concurrent.Future

object Authentifier {
  // Type mismatch is a false error from intellij idea
  def asAuthentified(route: UserEntry => server.Route)(implicit context: RestContext): Route =
    authenticateBasicAsync(Config.Api.authRealm, userPassAuthenticator(context))(route)

  private def userPassAuthenticator(context: RestContext)(credentials: Credentials): Future[Option[UserEntry]] =
    credentials match {
      case p@Provided(username) =>
        val result = (context.userManager ? GetUserEntryRequest(username)) (Config.Api.authTimeout).mapTo[GetUserEntryResult]
        import context.system.dispatcher
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

}
