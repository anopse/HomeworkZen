package homeworkzen.rest.routes.test

import akka.http.scaladsl.model._
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import homeworkzen.rest.Authentifier._
import homeworkzen.rest._
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type

object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("test") {
      get {
        asAuthentified { entry =>
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Hello ${entry.username} -> $thisType</h1>")): server.Route
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}
