package homeworkzen.rest

import akka.actor._
import akka.stream.ActorMaterializer

case class RestContext(userManager: ActorRef,
                       userCluster: ActorRef,
                       system: ActorSystem,
                       materializer: ActorMaterializer)
