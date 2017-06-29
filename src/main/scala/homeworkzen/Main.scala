package homeworkzen

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.cluster.Cluster
import akka.stream.ActorMaterializer
import homeworkzen.rest.RestContext
import homeworkzen.clustering.{UserManagerSingleton, UserWorkerSharding}

import scala.io.StdIn

object Main extends App {
  {
    println("Initializing server...")
    implicit val actorSystem = ActorSystem("homeworkzen")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher
    val userManager = UserManagerSingleton.register
    val userCluster = UserWorkerSharding.register
    //val clusterLogger = actorSystem.actorOf(Props(new ClusterLogger), "ClusterLogger")
    implicit val restContext = RestContext(userManager, userCluster, actorSystem, materializer)
    val httpBinding = homeworkzen.rest.Routes.bindRoutes

    CoordinatedShutdown(actorSystem).addTask(CoordinatedShutdown.PhaseServiceUnbind, "api unbind")(
      () => httpBinding.flatMap(_.unbind()).map(_ => Done)
    )

    val cluster = Cluster(actorSystem)
    cluster.join(cluster.selfAddress)

    println(s"Server online at http://${Config.Api.interface}:${Config.Api.port}/")
    println("Server initialization done.")
    println("Press RETURN to stop server...")
    StdIn.readLine()
    println("Stopping server...")
    CoordinatedShutdown(actorSystem).run()
    ()
  }

}
