package homeworkzen.rest

import akka.actor._
import akka.stream.ActorMaterializer
import homeworkzen.domain.query.JournalReader

case class RestContext(userManager: ActorRef,
                       userCluster: ActorRef)
                      (implicit val actorSystem: ActorSystem,
                       implicit val materializer: ActorMaterializer,
                       implicit val journalReader: JournalReader)
