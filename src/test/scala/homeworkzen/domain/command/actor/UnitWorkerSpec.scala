package homeworkzen.domain.command.actor

import java.util.UUID

import akka.actor.{ActorSystem, Props}
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

  "UnitWorker" should "accept basic deposit" in {
    val userId = UserId(UUID.fromString("d6c6858c-c67a-4e06-9025-4ab452d85873"))
    val unitId = UnitId(UUID.fromString("3af5cf13-b72c-4d31-b6ec-8e51ede06328"))
    val unitType = UnitType.Thermal
    val props = Props(new UnitWorker(userId, unitId, 2000, unitType))
    val unitWorker = actorSystem.actorOf(props, unitId.id.toString)
    val testProbe = TestProbe()
    val command = DepositCommand(userId, unitId, 1000)
    testProbe.send(unitWorker, command)
    testProbe.expectMsg(5000.milliseconds, DepositResult(command, Right(1000L)))
  }
}
