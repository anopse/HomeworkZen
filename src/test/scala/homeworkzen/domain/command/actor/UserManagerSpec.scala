package homeworkzen.domain.command.actor

import akka.actor.{ActorSystem, PoisonPill, Props, Terminated}
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import homeworkzen.domain.command.message._
import homeworkzen.domain.utils._
import org.scalatest.{FlatSpec, Matchers}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._

class UserManagerSpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigPreset.defaultWithPersistence)
  private implicit val actorMaterializer = ActorMaterializer()
  private val timeout = 5000.milliseconds

  "UserManager" should "accept basic user registration" in {
    val props = Props(new UserManager)
    val userManager = actorSystem.actorOf(props, "5c0acb7b-6be0-46ed-902e-4a63c4c16e85")
    val testProbe = TestProbe()

    // try create user
    {
      val command = CreateUserCommand("username", "hashedPassword")
      testProbe.send(userManager, command)
      val response = testProbe.expectMsgType[CreateUserResult](timeout)
      response.result.isRight shouldBe true
    }
  }

  it should "refuse to create two user with same username" in {
    val props = Props(new UserManager)
    val userManager = actorSystem.actorOf(props, "35f3c93e-2796-4e2d-b259-990e9701c2b1")
    val testProbe = TestProbe()

    // try create user
    {
      val command = CreateUserCommand("username", "hashedPassword")
      testProbe.send(userManager, command)
      val response = testProbe.expectMsgType[CreateUserResult](timeout)
      response.result.isRight shouldBe true
    }

    // try create same user again
    {
      val command = CreateUserCommand("username", "anotherHashedPassword")
      testProbe.send(userManager, command)
      val response = testProbe.expectMsgType[CreateUserResult](timeout)
      response shouldBe CreateUserResult(command, Left(UsernameAlreadyExist))
    }
  }

  it should "persist state" in {
    val props = Props(new UserManager)
    val actorName = "87d6963c-cd45-4079-899f-8d55bfdd9e32"
    var userManager = actorSystem.actorOf(props, actorName)
    val testProbe = TestProbe()

    // try create user
    {
      val command = CreateUserCommand("username", "hashedPassword")
      testProbe.send(userManager, command)
      val response = testProbe.expectMsgType[CreateUserResult](timeout)
      response.result.isRight shouldBe true
    }

    // kill then restart actor
    {
      testProbe.watch(userManager)
      userManager ! PoisonPill
      testProbe.expectMsg(timeout, Terminated(userManager)(existenceConfirmed = true, addressTerminated = true))
      userManager = actorSystem.actorOf(props, actorName)
    }

    // try create same user again
    {
      val command = CreateUserCommand("username", "anotherHashedPassword")
      testProbe.send(userManager, command)
      val response = testProbe.expectMsgType[CreateUserResult](timeout)
      response shouldBe CreateUserResult(command, Left(UsernameAlreadyExist))
    }
  }
}
