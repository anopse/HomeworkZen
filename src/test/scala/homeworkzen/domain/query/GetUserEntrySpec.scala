package homeworkzen.domain.query

import java.time.Instant
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.query.{EventEnvelope, Offset}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import homeworkzen.domain.command.message.UserCreatedEvent
import homeworkzen.model.{UserEntry, UserId}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import org.scalamock.scalatest.MockFactory

import scala.collection.immutable
import scala.concurrent.Await

class GetUserEntrySpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigFactory.empty())
  private implicit val actorMaterializer = ActorMaterializer()
  private val testUsername = "test"
  private val testUsernameBase64: String = homeworkzen.util.Base64.encodeString(testUsername)
  private val testId = UserId(UUID.randomUUID())
  private val testSecret = "secret"
  private val testUserEntry = UserEntry(testId, testUsername, testSecret)

  private def randomUserEntry: UserEntry = {
    val id = UserId(UUID.randomUUID())
    val username = "username-" + id.id.toString
    val secret = "secret-" + id.id.toString
    UserEntry(id, username, secret)
  }

  private def sourceFromUserEntries(userEntries: UserEntry*): Source[EventEnvelope, NotUsed] = {
    val events = userEntries.map(entry => {
      val created = UserCreatedEvent(Instant.MIN, entry)
      EventEnvelope(null, "", 0, created)
    })
    Source(events.to[immutable.Seq])
  }

  "GetUserEntry" should "return None with an empty journal" in {
    implicit val journal = mock[JournalReader]
    val emptySource = sourceFromUserEntries()
    journal.currentEventsByTag _ expects testUsernameBase64 returning emptySource
    val result = Await.result(GetUserEntry(testUsername), 500.milliseconds)
    result shouldBe None
  }

  it should "return correct user entry with filled journal" in {
    implicit val journal = mock[JournalReader]
    val filledSource = sourceFromUserEntries(randomUserEntry, testUserEntry, randomUserEntry)
    journal.currentEventsByTag _ expects testUsernameBase64 returning filledSource
    val result = Await.result(GetUserEntry(testUsername), 500.milliseconds)
    result shouldBe Some(testUserEntry)
  }
}
