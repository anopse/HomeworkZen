package homeworkzen

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown, Props}
import akka.stream.ActorMaterializer
import homeworkzen.domain.command.actor.UserManager
import homeworkzen.rest.RestContext
import homeworkzen.util.ClusterLogger

import scala.io.StdIn

object Main extends App {
  println("Initializing server...")
  private implicit val actorSystem = ActorSystem("homeworkzen")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = actorSystem.dispatcher
  private val userManager = actorSystem.actorOf(Props(new UserManager), "UserManager")
  private val clusterLogger = actorSystem.actorOf(Props(new ClusterLogger), "ClusterLogger")
  private implicit val restContext = RestContext(userManager, actorSystem, materializer)
  private val httpBinding = homeworkzen.rest.Routes.bindRoutes

  CoordinatedShutdown(actorSystem).addTask(CoordinatedShutdown.PhaseServiceUnbind, "api unbind")(
    () => httpBinding.flatMap(_.unbind()).map(_ => Done)
  )

  println(s"Server online at http://${Config.Api.interface}:${Config.Api.port}/")
  println("Server initialization done.")
  println("Press RETURN to stop server...")
  StdIn.readLine()
  println("Stopping server...")
  CoordinatedShutdown(actorSystem).run()
}
