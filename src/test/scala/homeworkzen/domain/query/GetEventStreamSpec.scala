package homeworkzen.domain.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import homeworkzen.domain.command.message.UserEvent
import homeworkzen.domain.utils._
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Await

class GetEventStreamSpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigPreset.default)
  private implicit val actorMaterializer = ActorMaterializer()

  "GetEventStream" should "only return events sent into source that match user" in {
    implicit val journal = mock[JournalReader]
    val globalSource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    journal.newEventsByTag _ expects user.userIdTag returning globalSource
    val resultStream = GetEventStream(user.userId)
    val resultFuture = resultStream.runFold(Nil: List[UserEvent])((list, event) => event::list)
    val result = Await.result(resultFuture, 500.milliseconds)
    // unordered collection equality test
    result.toSet shouldBe user.userIdJournal.toSet
  }
}
