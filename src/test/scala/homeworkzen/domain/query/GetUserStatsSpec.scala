package homeworkzen.domain.query


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import homeworkzen.domain.utils._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.Await

class GetUserStatsSpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigPreset.default)
  private implicit val actorMaterializer = ActorMaterializer()

  "GetUserStats" should "return empty stats with an empty journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(Seq.empty)
    val user = TestData.userWithUnits
    journal.currentEventsByTag _ expects user.userIdTag returning emptySource
    val result = Await.result(GetUserStats(user.userId), 500.milliseconds)
    result shouldBe TestData.specialValues.emptyUserStats
  }

  it should "return stats with a filled journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    journal.currentEventsByTag _ expects user.userIdTag returning emptySource
    val result = Await.result(GetUserStats(user.userId), 500.milliseconds)
    result shouldBe user.stats
  }
}