package homeworkzen.clustering

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.singleton._
import homeworkzen.domain.command.actor.UserManager

object UserManagerSingleton {
  def register(implicit actorSystem: ActorSystem): ActorRef = {
    val singletonSettings = ClusterSingletonManagerSettings(actorSystem)
    val singletonManagerProps = ClusterSingletonManager.props(Props(new UserManager), PoisonPill, singletonSettings)
    val singletonManager = actorSystem.actorOf(singletonManagerProps, "UserManagerSingletonManager")
    val proxy = actorSystem.actorOf(ClusterSingletonProxy.props(
      singletonManagerPath = singletonManager.path.toStringWithoutAddress,
      settings = ClusterSingletonProxySettings(actorSystem)),
      name = "UserManagerProxy")
    proxy
  }
}
