package homeworkzen.domain.query

import java.time.Instant
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.query.EventEnvelope
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import homeworkzen.domain.command.message.UserCreatedEvent
import homeworkzen.domain.utils._
import homeworkzen.model.{UserEntry, UserId}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Await

class GetUserEntrySpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigFactory.empty())
  private implicit val actorMaterializer = ActorMaterializer()

  "GetUserEntry" should "return None with an empty journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = SourceBuilder.sourceFromSeq(Seq.empty)
    val user = TestData.userWithoutUnits
    journal.currentEventsByTag _ expects user.usernameTag returning emptySource
    val result = Await.result(GetUserEntry(user.username), 500.milliseconds)
    result shouldBe None
  }

  it should "return correct user entry with filled journal" in {
    implicit val journal = mock[JournalReader]
    val filledSource = SourceBuilder.sourceFromSeq(TestData.globalJournal)
    val user = TestData.userWithUnits
    journal.currentEventsByTag _ expects user.usernameTag returning filledSource
    val result = Await.result(GetUserEntry(user.username), 500.milliseconds)
    result shouldBe Some(user.userEntry)
  }

  it should "return None with journal filled only with non-matching user" in {
    implicit val journal = mock[JournalReader]
    val nonMatchingSource = SourceBuilder.sourceFromSeq(TestData.userWithUnits.usernameJournal)
    val user = TestData.anotherUserWithUnits
    journal.currentEventsByTag _ expects user.usernameTag returning nonMatchingSource
    val result = Await.result(GetUserEntry(user.username), 500.milliseconds)
    result shouldBe None
  }
}
