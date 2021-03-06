package homeworkzen.rest

import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import homeworkzen.rest.swagger.SwaggerRoute
import homeworkzen.{Config, rest}

import scala.concurrent.Future

object Routes {
  def bindRoutes(implicit context: RestContext): Future[Http.ServerBinding] = {
    implicit val system = context.actorSystem
    implicit val materializer = context.materializer
    val rest = routes.map(_.route).reduce(_ ~ _)
    val swagger = SwaggerRoute.getRoute
    val handler = rest ~ swagger
    Http().bindAndHandle(handler, Config.Api.interface, Config.Api.port)
  }

  def routes: Seq[RestRoute] =
    List(rest.routes.users.Post,
      rest.routes.stations.Get,
      rest.routes.stations.Post,
      rest.routes.stations.id.Get,
      rest.routes.stations.id.deposit.Post,
      rest.routes.stations.id.withdraw.Post,
      rest.routes.stations.id.history.Get,
      rest.routes.stations.id.stats.Get,
      rest.routes.stations.stats.Get,
      rest.routes.stations.events.Get
    )

}
