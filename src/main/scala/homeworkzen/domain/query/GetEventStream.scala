package homeworkzen.domain.query

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import homeworkzen.domain.command.message._
import homeworkzen.domain.utils.QueryHelper
import homeworkzen.model._

object GetEventStream {
  def apply(userId: UserId)(implicit actorSystem: ActorSystem): Source[UserEvent, NotUsed] = {
    QueryHelper.newEventsByTag(userId.id.toString)(actorSystem)
      .map(_.event)
      .collect { case event: UserEvent => event }
      .filter(_.userId == userId)
  }
}
