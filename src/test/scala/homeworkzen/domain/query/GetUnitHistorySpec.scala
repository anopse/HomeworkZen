package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import homeworkzen.domain.utils.{SourceBuilder, TestData}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.duration._

import scala.concurrent.Await

class GetUnitHistorySpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigFactory.empty())
  private implicit val actorMaterializer = ActorMaterializer()

  "GetUnitHistory" should "return empty collection with an empty journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(Seq.empty)
    val user = TestData.userWithUnits
    val unit = user.unitWithOperations
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetUnitHistory(user.userId, unit.unitId, None, None), 500.milliseconds)
    result shouldBe Seq.empty
  }

  it should "return empty collection if asked unit from another user" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    val unit = TestData.anotherUserWithUnits.unitWithOperations
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetUnitHistory(user.userId, unit.unitId, None, None), 500.milliseconds)
    result shouldBe Seq.empty
  }

  it should "return complete history with no from/to specified" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    val unit = user.unitWithHistoryData
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetUnitHistory(user.userId, unit.unitId, None, None), 500.milliseconds)
    result shouldBe unit.completeHistory
  }

  it should "return only partial history with 'from' or 'to' specified (1)" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    val unit = user.unitWithHistoryData
    val from = unit.partialHistory1From
    val to = unit.partialHistory1To
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetUnitHistory(user.userId, unit.unitId, from, to), 500.milliseconds)
    result shouldBe unit.partialHistory1
  }

  it should "return only partial history with 'from' or 'to' specified (2)" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    val unit = user.unitWithHistoryData
    val from = unit.partialHistory2From
    val to = unit.partialHistory2To
    journal.currentEventsByTag _ expects unit.unitTag returning emptySource
    val result = Await.result(GetUnitHistory(user.userId, unit.unitId, from, to), 500.milliseconds)
    result shouldBe unit.partialHistory2
  }
}