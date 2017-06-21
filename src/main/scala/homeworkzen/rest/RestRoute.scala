package homeworkzen.rest

import akka.http.scaladsl.server

trait RestRoute {
  def route(implicit context: RestContext): server.Route
}