package homeworkzen.rest

import akka.http.scaladsl.server
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials.Provided
import akka.http.scaladsl.server.directives.SecurityDirectives._
import akka.http.scaladsl.server.directives._
import homeworkzen.Config
import homeworkzen.domain.query.GetUserEntry
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
        import context._
        val result = GetUserEntry(username)
        result.map(_.filter(entry => p.verify(entry.secret, Hasher.apply)))(actorSystem.dispatcher)
      case _ => Future.successful(None)
    }

}
