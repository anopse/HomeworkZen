package homeworkzen.rest

import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import homeworkzen.Config

import scala.concurrent.Future

object Routes {
  def bindRoutes(implicit context: RestContext): Future[Http.ServerBinding] = {
    implicit val system = context.system
    implicit val materializer = context.materializer
    val swagger = (new SwaggerDocService).routes
    val swaggerUI = path("swagger") {
      getFromResource("swagger/index.html")
    } ~ getFromResourceDirectory("swagger")
    val rest = routes.map(_.route).reduce(_ ~ _)
    val handler = rest ~ swagger ~ swaggerUI
    Http().bindAndHandle(handler, Config.Api.interface, Config.Api.port)
  }

  def routes: Seq[RestRoute] =
    List(homeworkzen.rest.routes.users.Post,
      homeworkzen.rest.routes.test.Get)

}
