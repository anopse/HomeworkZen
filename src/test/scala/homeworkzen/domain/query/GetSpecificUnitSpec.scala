package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import homeworkzen.domain.utils.{SourceBuilder, TestData}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.duration._

import scala.concurrent.Await

class GetSpecificUnitSpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigFactory.empty())
  private implicit val actorMaterializer = ActorMaterializer()

  "GetSpecificUnit" should "return None with an empty journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(Seq.empty)
    val user = TestData.userWithUnits
    val unit = user.unitWithOperations
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetSpecificUnit(user.userId, unit.unitId), 500.milliseconds)
    result shouldBe None
  }

  it should "return None if asked unit from another user" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    val unit = TestData.anotherUserWithUnits.unitWithOperations
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetSpecificUnit(user.userId, unit.unitId), 500.milliseconds)
    result shouldBe None
  }

  it should "return matching unit with a filled journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    val unit = user.unitWithOperations
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetSpecificUnit(user.userId, unit.unitId), 500.milliseconds)
    result shouldBe Some(unit.unitInfo)
  }
}