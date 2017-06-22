package homeworkzen.rest

import akka.http.scaladsl.server

import scala.reflect.runtime.universe._

trait RestRoute {
  def route(implicit context: RestContext): server.Route

  def thisType: Type
}