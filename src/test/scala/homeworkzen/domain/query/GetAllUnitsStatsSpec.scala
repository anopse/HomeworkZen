package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import homeworkzen.domain.utils._
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Await

class GetAllUnitsStatsSpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigFactory.empty())
  private implicit val actorMaterializer = ActorMaterializer()

  "GetAllUnitsStats" should "return empty collection with an empty journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(Seq.empty)
    val user = TestData.userWithUnits
    journal.currentEventsByTag _ expects user.userIdTag returning emptySource
    val result = Await.result(GetAllUnitsStats(user.userId), 500.milliseconds)
    result shouldBe Seq.empty
  }

  it should "return all matching units stats with a filled journal" in {
    implicit val journal = mock[JournalReader]
    val globalSource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.anotherUserWithUnits
    journal.currentEventsByTag _ expects user.userIdTag returning globalSource
    val result = Await.result(GetAllUnitsStats(user.userId), 500.milliseconds)
    result shouldBe user.units.map(_.stats)
  }
}
