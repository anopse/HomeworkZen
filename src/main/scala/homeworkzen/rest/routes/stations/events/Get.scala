package homeworkzen.rest.routes.stations.events

import akka.http.scaladsl.model.sse.ServerSentEvent

import scala.concurrent.duration._
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import homeworkzen.domain.query.GetEventStream
import homeworkzen.model.UserEntry
import homeworkzen.rest.Authentifier.asAuthentified
import homeworkzen.rest._
import homeworkzen.rest.dto.events.EventMapper
import homeworkzen.util.TypeHelper

import scala.reflect.runtime.universe.Type


object Get extends RestRoute {
  override def route(implicit context: RestContext): Route =
    path("stations" / "events") {
      get {
        asAuthentified { entry: UserEntry =>
          complete {
            GetEventStream(entry.id)(context.system)
              .collect(EventMapper.modelEventToDTO)
              .collect(EventMapper.dtoToJson)
              .map(ServerSentEvent(_))
              .keepAlive(5.second, () => ServerSentEvent.heartbeat)
          }
        }
      }
    }

  override def thisType: Type = TypeHelper.getType(this)
}
