package homeworkzen

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import homeworkzen.domain.actor.UserManager

import scala.io.StdIn

object Main extends App {
  println("Initializing server...")
  private implicit val actorSystem = ActorSystem("homeworkzen")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = actorSystem.dispatcher
  private val userManager = actorSystem.actorOf(Props(new UserManager), "UserManager")
  private val httpBinding = homeworkzen.rest.Routes.bindRoutes(userManager)(actorSystem, materializer)
  println(s"Server online at http://${Config.Api.interface}:${Config.Api.port}/")
  println("Server initialization done.")
  println("Press RETURN to stop server...")
  StdIn.readLine()
  println("Stopping server...")
  httpBinding.flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => actorSystem.terminate()) // and shutdown when done
  println("Server stopped.")
}
