package homeworkzen.rest.swagger

import akka.http.scaladsl.server.Directives.{getFromResource, getFromResourceDirectory, path}
import homeworkzen.rest.{RestContext, SwaggerDocService}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object SwaggerRoute {
  def getRoute(implicit restContext: RestContext): Route = {
    val swagger = (new SwaggerDocService).routes
    val swaggerUI = path("swagger") {
      getFromResource("swagger/index.html")
    } ~ getFromResourceDirectory("swagger")
    swagger ~ swaggerUI
  }
}
