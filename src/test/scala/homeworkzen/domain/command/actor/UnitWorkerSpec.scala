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

class UnitWorkerSpec extends FlatSpec with Matchers with MockFactory {
  private implicit val actorSystem = ActorSystem("test", ConfigPreset.defaultWithPersistence)
  private implicit val actorMaterializer = ActorMaterializer()
  private val timeout = 5000.milliseconds

  "UnitWorker" should "accept basic deposit" in {
    val userId = UserId(UUID.fromString("d6c6858c-c67a-4e06-9025-4ab452d85873"))
    val unitId = UnitId(UUID.fromString("3af5cf13-b72c-4d31-b6ec-8e51ede06328"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // try deposit 1000
    {
      val command = DepositCommand(userId, unitId, 1000)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(1000L)))
    }
  }

  it should "refuse deposits not strictly positive" in {
    val userId = UserId(UUID.fromString("abbf1d17-9011-4eb6-a19b-17bc46485f8a"))
    val unitId = UnitId(UUID.fromString("04c022c9-7e33-4800-bef4-455cda58b01f"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // start with an amount of 1000
    {
      val command = DepositCommand(userId, unitId, 1000)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(1000L)))
    }

    // try deposit 0
    {
      val command = DepositCommand(userId, unitId, 0)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Left(InvalidDepositAmount)))
    }

    // try deposit -1
    {
      val command = DepositCommand(userId, unitId, -1)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Left(InvalidDepositAmount)))
    }
  }

  it should "refuse withdraw when empty" in {
    val userId = UserId(UUID.fromString("dd4e4005-3b64-4851-9165-5b894e84025f"))
    val unitId = UnitId(UUID.fromString("ea265aa3-cd5f-4f3f-b688-7c46eca370ce"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // try withdrawal of 1
    {
      val command = WithdrawCommand(userId, unitId, 1)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, WithdrawResult(command, Left(WithdrawExceedAvailableAmount)))
    }
  }

  it should "accept basic withdrawal after deposit made" in {
    val userId = UserId(UUID.fromString("6861915d-9558-4e52-8a95-aeb8d7b9dc5d"))
    val unitId = UnitId(UUID.fromString("cfac7614-ead1-4116-93c3-2c53abd7872a"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // start with an amount of 1000
    {
      val command = DepositCommand(userId, unitId, 1000)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(1000L)))
    }

    // try withdrawal of 1
    {
      val command = WithdrawCommand(userId, unitId, 1)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, WithdrawResult(command, Right(999L)))
    }
  }

  it should "refuse withdrawal higher than current amount" in {
    val userId = UserId(UUID.fromString("8590fa07-8a9c-4e25-90dd-807c53a4334f"))
    val unitId = UnitId(UUID.fromString("99a766df-fc4a-4f43-93fc-b4e26438d30a"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // start with an amount of 1000
    {
      val command = DepositCommand(userId, unitId, 1000)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(1000L)))
    }

    // try withdrawal of 1001
    {
      val command = WithdrawCommand(userId, unitId, 1001)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, WithdrawResult(command, Left(WithdrawExceedAvailableAmount)))
    }
  }

  it should "refuse withdrawal not strictly positive" in {
    val userId = UserId(UUID.fromString("e088c28b-10fd-4ce1-a100-ba26c024a7ff"))
    val unitId = UnitId(UUID.fromString("d3289989-1208-42d8-80fa-a48222b28644"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // start with an amount of 1000
    {
      val command = DepositCommand(userId, unitId, 1000)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(1000L)))
    }

    // try withdrawal of 0
    {
      val command = WithdrawCommand(userId, unitId, 0)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, WithdrawResult(command, Left(InvalidWithdrawAmount)))
    }

    // try withdrawal of -1
    {
      val command = WithdrawCommand(userId, unitId, -1)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, WithdrawResult(command, Left(InvalidWithdrawAmount)))
    }
  }

  it should "refuse deposit higher than would exceeds maximum capacity" in {
    val userId = UserId(UUID.fromString("10260ead-5af7-45f1-8860-9058a2e50a2c"))
    val unitId = UnitId(UUID.fromString("e021be84-f596-4b8c-aba1-6deca3a48679"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // start with an amount of 2000
    {
      val command = DepositCommand(userId, unitId, 2000)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(2000L)))
    }

    // try to deposit 1
    {
      val command = DepositCommand(userId, unitId, 1)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Left(DepositExceedCapacity)))
    }
  }

  it should "persist state" in {
    val userId = UserId(UUID.fromString("ca5bcff3-8705-473d-b274-88bf40491dd3"))
    val unitId = UnitId(UUID.fromString("67a4016c-27bd-43c9-a1c2-79222bd9ab8e"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    var unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()

    // deposit 2000
    {
      val command = DepositCommand(userId, unitId, 2000)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(2000L)))
    }

    // withdraw 500
    {
      val command = WithdrawCommand(userId, unitId, 500)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, WithdrawResult(command, Right(1500L)))
    }

    // kill then restart actor
    {
      testProbe.watch(unitWorker)
      unitWorker ! PoisonPill
      testProbe.expectMsg(timeout, Terminated(unitWorker)(existenceConfirmed = true, addressTerminated = true))
      unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    }

    // try to deposit 1 to check current amount
    {
      val command = DepositCommand(userId, unitId, 1)
      testProbe.send(unitWorker, command)
      testProbe.expectMsg(timeout, DepositResult(command, Right(1501L)))
    }
  }
}
