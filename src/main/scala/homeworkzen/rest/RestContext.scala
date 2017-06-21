package homeworkzen.rest

import akka.actor._

case class RestContext(userManager: ActorRef, system: ActorSystem)
