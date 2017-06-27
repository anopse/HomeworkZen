package homeworkzen.clustering

import akka.actor.{ActorRef, ActorSystem, Props}
import homeworkzen.domain.command.actor._
import homeworkzen.domain.command.message._
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import homeworkzen.Config

object UserWorkerSharding {
  def register(implicit system: ActorSystem): ActorRef =
    ClusterSharding(system).start(
      "UserWorker",
      Props(new UserWorker),
      ClusterShardingSettings(system),
      idExtractor,
      shardResolver)

  def props(): Props = Props(new UserWorker)

  val idExtractor: ShardRegion.ExtractEntityId = {
    case msg: UserCommand => (msg.userId.id.toString, msg)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case msg: UserCommand => (math.abs(msg.userId.id.hashCode()) % Config.Cluster.shardCount).toString
  }

  val shardName: String = "UserSharding"
}
