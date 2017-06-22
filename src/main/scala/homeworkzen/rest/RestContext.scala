package homeworkzen.rest

import akka.actor._
import akka.stream.ActorMaterializer

case class RestContext(userManager: ActorRef, system: ActorSystem, materializer: ActorMaterializer)
