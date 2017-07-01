package homeworkzen

import akka.Done
import akka.actor.{ActorSystem, Address, CoordinatedShutdown}
import akka.cluster.Cluster
import akka.stream.ActorMaterializer
import homeworkzen.rest.RestContext
import homeworkzen.clustering.{UserManagerSingleton, UserWorkerSharding}
import homeworkzen.domain.query.JdbcJournalReader

import scala.io.StdIn

object Main extends App {
  {
    println("Initializing server...")
    implicit val actorSystem = ActorSystem("homeworkzen")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher
    implicit val userManager = UserManagerSingleton.register
    implicit val userCluster = UserWorkerSharding.register
    implicit val journalReader = new JdbcJournalReader
    //val clusterLogger = actorSystem.actorOf(Props(new ClusterLogger), "ClusterLogger")
    implicit val restContext = RestContext(userManager, userCluster)
    val httpBinding = homeworkzen.rest.Routes.bindRoutes

    CoordinatedShutdown(actorSystem).addTask(CoordinatedShutdown.PhaseServiceUnbind, "api unbind")(
      () => httpBinding.flatMap(_.unbind()).map(_ => Done)
    )

    val cluster = Cluster(actorSystem)
    if (args.length == 3) {
      // todo improve args handling
      // for testing purpose only for now
      val addr = Address(cluster.selfAddress.protocol, cluster.selfAddress.system, args(3), args(4).toInt)
      cluster.join(addr)
    }
    else
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
