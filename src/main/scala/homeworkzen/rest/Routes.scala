package homeworkzen.rest

import akka.actor._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import homeworkzen.Config

import scala.concurrent.Future

object Routes {

  def bindRoutes(userManager: ActorRef)(implicit system: ActorSystem, materializer: ActorMaterializer): Future[Http.ServerBinding] = {
    val context = RestContext(userManager, system)
    val handler = routes.map(_.route(context)).reduce(_ ~ _)
    Http().bindAndHandle(handler, Config.Api.interface, Config.Api.port)
  }


  private def routes: List[RestRoute] =
    List(homeworkzen.rest.routes.users.Post,
      homeworkzen.rest.routes.test.Get)

}
