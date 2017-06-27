package homeworkzen

import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Config {

  object Api {
    lazy val port: Int = ConfigFactory.load().getInt("homeworkzen.api.port")
    lazy val interface: String = ConfigFactory.load().getString("homeworkzen.api.interface")
    lazy val authRealm: String = ConfigFactory.load().getString("homeworkzen.api.authRealm")
    lazy val askTimeout: Timeout = Timeout(ConfigFactory.load().getLong("homeworkzen.api.askTimeout").milliseconds)
    lazy val hashSalt: String = ConfigFactory.load().getString("homeworkzen.api.hashSalt")
  }

  object Cluster {
    lazy val shardCount: Int = ConfigFactory.load().getInt("homeworkzen.cluster.shardCount")
  }


}
