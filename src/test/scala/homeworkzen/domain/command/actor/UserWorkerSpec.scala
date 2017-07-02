package homeworkzen.domain.command.actor


import java.util.UUID

import akka.actor.{ActorSystem, PoisonPill, Props, Terminated}
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import homeworkzen.domain.command.message._
import homeworkzen.domain.utils._
import homeworkzen.model._
import org.scalatest.{FlatSpec, Matchers}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._

class UserWorkerSpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigPreset.defaultWithPersistence)
  private implicit val actorMaterializer = ActorMaterializer()
  private val timeout = 5000.milliseconds

  "UserWorker" should "accept basic unit creation" in {
    val userId = UserId(UUID.fromString("57d63edb-99a7-4cd2-bcf3-22e9ac91f194"))
    val unitType = UnitType.Thermal
    val props = Props(new UserWorker)
    val userWorker = actorSystem.actorOf(props, userId.id.toString)
    val testProbe = TestProbe()

    // try create unit
    {
      val command = CreateUnitCommand(userId, 1000, unitType)
      testProbe.send(userWorker, command)
      val response = testProbe.expectMsgType[CreateUnitResult](timeout)
      response.result.isRight shouldBe true
    }
  }

  it should "refuse to create unit with maximum capacity not stricly positive" in {
    val userId = UserId(UUID.fromString("d33d4466-9ab8-4c72-94a8-feafb9e996eb"))
    val unitType = UnitType.Thermal
    val props = Props(new UserWorker)
    val userWorker = actorSystem.actorOf(props, userId.id.toString)
    val testProbe = TestProbe()

    // try create unit with maximum capacity of 0
    {
      val command = CreateUnitCommand(userId, 0, unitType)
      testProbe.send(userWorker, command)
      val response = testProbe.expectMsgType[CreateUnitResult](timeout)
      response shouldBe CreateUnitResult(command, Left(InvalidMaximumCapacityValue))
    }

    // try create unit with maximum capacity of -1
    {
      val command = CreateUnitCommand(userId, -1, unitType)
      testProbe.send(userWorker, command)
      val response = testProbe.expectMsgType[CreateUnitResult](timeout)
      response shouldBe CreateUnitResult(command, Left(InvalidMaximumCapacityValue))
    }
  }

  it should "forward unit commands to child" in {
    val userId = UserId(UUID.fromString("6ea6ec5e-cae1-4667-accd-fdc38c693025"))
    val unitType = UnitType.Thermal
    val props = Props(new UserWorker)
    val userWorker = actorSystem.actorOf(props, userId.id.toString)
    val testProbe = TestProbe()

    // create unit
    val unitId = {
      val command = CreateUnitCommand(userId, 1000, unitType)
      testProbe.send(userWorker, command)
      val response = testProbe.expectMsgType[CreateUnitResult](timeout)
      response.result.isRight shouldBe true
      response.result.right.get
    }

    // try deposit 500
    {
      val command = DepositCommand(userId, unitId, 500)
      testProbe.send(userWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(500L)))
    }
  }

  it should "refuse to forward unit commands to non-existant child" in {
    val userId = UserId(UUID.fromString("8cad00ef-de7a-40ac-9652-50fed21d693f"))
    val unitType = UnitType.Thermal
    val props = Props(new UserWorker)
    val userWorker = actorSystem.actorOf(props, userId.id.toString)
    val testProbe = TestProbe()

    // create a unit
    {
      val command = CreateUnitCommand(userId, 1000, unitType)
      testProbe.send(userWorker, command)
      val response = testProbe.expectMsgType[CreateUnitResult](timeout)
      response.result.isRight shouldBe true
    }

    // try deposit 500 to imaginary unit
    {
      val imaginaryUnitId = UnitId(UUID.fromString("f9ef67f2-0c00-4719-a132-1aeeb8d491d6"))
      val command = DepositCommand(userId, imaginaryUnitId, 500)
      testProbe.send(userWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Left(DepositUnitNotFound)))
    }
  }

  it should "persist state" in {
    val userId = UserId(UUID.fromString("22aaef61-23fc-4e9e-83fb-7c1efb3ce5b7"))
    val unitType = UnitType.Thermal
    val props = Props(new UserWorker)
    var userWorker = actorSystem.actorOf(props, userId.id.toString)
    val testProbe = TestProbe()

    // create unit
    val unitId = {
      val command = CreateUnitCommand(userId, 1000, unitType)
      testProbe.send(userWorker, command)
      val response = testProbe.expectMsgType[CreateUnitResult](timeout)
      response.result.isRight shouldBe true
      response.result.right.get
    }

    // try deposit 500
    {
      val command = DepositCommand(userId, unitId, 500)
      testProbe.send(userWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(500L)))
    }

    // kill then restart actor
    {
      testProbe.watch(userWorker)
      userWorker ! PoisonPill
      testProbe.expectMsg(timeout, Terminated(userWorker)(existenceConfirmed = true, addressTerminated = true))
      userWorker = actorSystem.actorOf(props, userId.id.toString)
    }

    // try deposit 1
    {
      val command = DepositCommand(userId, unitId, 1)
      testProbe.send(userWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(501L)))
    }
  }
}
