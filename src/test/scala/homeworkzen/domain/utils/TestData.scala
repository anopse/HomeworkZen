package homeworkzen.domain.utils

import java.time.Instant
import java.util.UUID

import homeworkzen.domain.command.message._
import homeworkzen.model._
import homeworkzen.util.Base64

//noinspection TypeAnnotation
object TestData {
  val userWithUnits = new {
    private val name = "userWithUnits"
    val userId = UserId(UUID.fromString("6a6eeebf-791b-433c-ba3b-5fea6ab04b28"))
    val username: String = name + "_username"
    val secret: String = name + "_secret"
    val userEntry = UserEntry(userId, username, secret)
    val userCreationInstant: Instant = Instant.parse("2010-01-01T10:01:00Z")
    val creationEvent = UserCreatedEvent(userCreationInstant, userEntry)
    val usernameTag: String = Base64.encodeString(username)
    val userIdTag: String = userId.id.toString

    val justCreatedUnit = new {
      val unitType = UnitType.Eolian
      val unitId = UnitId(UUID.fromString("a57f9235-4922-44a8-bd41-296e24254a91"))
      val maximumAmount = 4200
      val unitCreationInstant: Instant = Instant.parse("2010-01-01T10:02:00Z")
      val creationEvent = UnitCreatedEvent(unitCreationInstant, userId, unitId, maximumAmount, unitType)
      val unitTag: String = unitId.id.toString
      val deposits: Seq[DepositEvent] = Seq.empty
      val withdrawals: Seq[WithdrawEvent] = Seq.empty
      val journal: Seq[Event] = Seq(creationEvent) ++ deposits ++ withdrawals
      val initialAmount: Long = deposits.map(_.amountDeposited).sum - withdrawals.map(_.amountWithdrawn).sum
      val unitInfo: UnitInfo = UnitInfo(unitId, unitType, maximumAmount, initialAmount)
      val totalConsumed = withdrawals.map(_.amountWithdrawn).sum
      val totalGenerated = deposits.map(_.amountDeposited).sum
      val stats = UnitStats(unitId, unitType, totalConsumed, totalGenerated)
    }

    val unitWithOperations = new {
      val unitType = UnitType.Solar
      val unitId = UnitId(UUID.fromString("2b5d5112-f267-42f8-8168-56ed676f8c2e"))
      val maximumAmount = 2400
      val unitCreationInstant: Instant = Instant.parse("2010-01-01T10:03:00Z")
      val creationEvent = UnitCreatedEvent(unitCreationInstant, userId, unitId, maximumAmount, unitType)
      val unitTag: String = unitId.id.toString
      val deposits: Seq[DepositEvent] = Seq(
        DepositEvent(Instant.parse("2010-01-01T10:04:00Z"), userId, unitId, 300),
        DepositEvent(Instant.parse("2010-01-01T10:05:00Z"), userId, unitId, 500)
      )
      val withdrawals: Seq[WithdrawEvent] = Seq(
        WithdrawEvent(Instant.parse("2010-01-01T10:06:00Z"), userId, unitId, 100),
        WithdrawEvent(Instant.parse("2010-01-01T10:07:00Z"), userId, unitId, 400)
      )
      val journal: Seq[Event] = Seq(creationEvent) ++ deposits ++ withdrawals
      val initialAmount: Long = deposits.map(_.amountDeposited).sum - withdrawals.map(_.amountWithdrawn).sum
      val unitInfo: UnitInfo = UnitInfo(unitId, unitType, maximumAmount, initialAmount)
      val totalConsumed = withdrawals.map(_.amountWithdrawn).sum
      val totalGenerated = deposits.map(_.amountDeposited).sum
      val stats = UnitStats(unitId, unitType, totalConsumed, totalGenerated)
    }

    val units = Seq(justCreatedUnit, unitWithOperations)
    val statsByUnitType = units.groupBy(_.unitType).mapValues(values => {
      val totalConsumed = values.map(_.totalConsumed).sum
      val totalGenerated = values.map(_.totalGenerated).sum
      val count = values.length
      GroupedStats(totalConsumed, totalGenerated, count)
    })
    val globalStat = {
      val totalConsumed = units.map(_.totalConsumed).sum
      val totalGenerated = units.map(_.totalGenerated).sum
      val count = units.length
      GroupedStats(totalConsumed, totalGenerated, count)
    }
    val stats = UserStats(globalStat, statsByUnitType, units.map(_.stats))
    val usernameJournal: Seq[Event] = Seq(creationEvent)
    val userIdJournal: Seq[Event] = usernameJournal ++ units.flatMap(_.journal)
  }

  val userWithoutUnits = new {
    private val name = "userWithoutUnits"
    val userId = UserId(UUID.fromString("50cd570b-a20b-4672-9903-6c59ae4b5dda"))
    val username: String = name + "_username"
    val secret: String = name + "_secret"
    val userEntry = UserEntry(userId, username, secret)
    val userCreationInstant: Instant = Instant.parse("2010-01-01T10:08:00Z")
    val creationEvent = UserCreatedEvent(userCreationInstant, userEntry)
    val usernameTag: String = Base64.encodeString(username)
    val userIdTag: String = userId.id.toString

    val units = userWithUnits.units.filter(_ => false) // typed empty seq
    val statsByUnitType = units.groupBy(_.unitType).mapValues(values => {
      val totalConsumed = values.map(_.totalConsumed).sum
      val totalGenerated = values.map(_.totalGenerated).sum
      val count = values.length
      GroupedStats(totalConsumed, totalGenerated, count)
    })
    val globalStat = {
      val totalConsumed = units.map(_.totalConsumed).sum
      val totalGenerated = units.map(_.totalGenerated).sum
      val count = units.length
      GroupedStats(totalConsumed, totalGenerated, count)
    }
    val stats = UserStats(globalStat, statsByUnitType, units.map(_.stats))
    val usernameJournal: Seq[Event] = Seq(creationEvent)
    val userIdJournal: Seq[Event] = usernameJournal ++ units.flatMap(_.journal)
  }

  val anotherUserWithoutUnits = new {
    private val name = "anotherUserWithoutUnits"
    val userId = UserId(UUID.fromString("62502475-9cbb-4659-b318-3edcf0fd0c55"))
    val username: String = name + "_username"
    val secret: String = name + "_secret"
    val userEntry = UserEntry(userId, username, secret)
    val userCreationInstant: Instant = Instant.parse("2010-01-01T10:09:00Z")
    val creationEvent = UserCreatedEvent(userCreationInstant, userEntry)
    val usernameTag: String = Base64.encodeString(username)
    val userIdTag: String = userId.id.toString

    val units = userWithUnits.units.filter(_ => false) // typed empty seq
    val statsByUnitType = units.groupBy(_.unitType).mapValues(values => {
      val totalConsumed = values.map(_.totalConsumed).sum
      val totalGenerated = values.map(_.totalGenerated).sum
      val count = values.length
      GroupedStats(totalConsumed, totalGenerated, count)
    })
    val globalStat = {
      val totalConsumed = units.map(_.totalConsumed).sum
      val totalGenerated = units.map(_.totalGenerated).sum
      val count = units.length
      GroupedStats(totalConsumed, totalGenerated, count)
    }
    val stats = UserStats(globalStat, statsByUnitType, units.map(_.stats))
    val usernameJournal: Seq[Event] = Seq(creationEvent)
    val userIdJournal: Seq[Event] = usernameJournal ++ units.flatMap(_.journal)
  }

  val anotherUserWithUnits = new {
    private val name = "anotherUserWithUnits"
    val userId = UserId(UUID.fromString("9df4d878-569e-4a15-a55d-a49415a6845d"))
    val username: String = name + "_username"
    val secret: String = name + "_secret"
    val userEntry = UserEntry(userId, username, secret)
    val userCreationInstant: Instant = Instant.parse("2010-01-01T10:10:00Z")
    val creationEvent = UserCreatedEvent(userCreationInstant, userEntry)
    val usernameTag: String = Base64.encodeString(username)
    val userIdTag: String = userId.id.toString

    val justCreatedUnit = new {
      val unitType = UnitType.Thermal
      val unitId = UnitId(UUID.fromString("06e26413-09ee-420a-8ae8-07b83f4c2ca0"))
      val maximumAmount = 42000
      val unitCreationInstant: Instant = Instant.parse("2010-01-01T10:11:00Z")
      val creationEvent = UnitCreatedEvent(unitCreationInstant, userId, unitId, maximumAmount, unitType)
      val unitTag: String = unitId.id.toString
      val deposits: Seq[DepositEvent] = Seq.empty
      val withdrawals: Seq[WithdrawEvent] = Seq.empty
      val journal: Seq[Event] = Seq(creationEvent) ++ deposits ++ withdrawals
      val initialAmount: Long = deposits.map(_.amountDeposited).sum - withdrawals.map(_.amountWithdrawn).sum
      val unitInfo: UnitInfo = UnitInfo(unitId, unitType, maximumAmount, initialAmount)
      val totalConsumed = withdrawals.map(_.amountWithdrawn).sum
      val totalGenerated = deposits.map(_.amountDeposited).sum
      val stats = UnitStats(unitId, unitType, totalConsumed, totalGenerated)
    }

    val unitWithOperations = new {
      val unitType = UnitType.Thermal
      val unitId = UnitId(UUID.fromString("5e94367a-613c-4fa0-a5dc-ea3b9add662d"))
      val maximumAmount = 24000
      val unitCreationInstant: Instant = Instant.parse("2010-01-01T10:12:00Z")
      val creationEvent = UnitCreatedEvent(unitCreationInstant, userId, unitId, maximumAmount, unitType)
      val unitTag: String = unitId.id.toString
      val deposits: Seq[DepositEvent] = Seq(
        DepositEvent(Instant.parse("2010-01-01T10:13:00Z"), userId, unitId, 700),
        DepositEvent(Instant.parse("2010-01-01T10:14:00Z"), userId, unitId, 200)
      )
      val withdrawals: Seq[WithdrawEvent] = Seq(
        WithdrawEvent(Instant.parse("2010-01-01T10:15:00Z"), userId, unitId, 300),
        WithdrawEvent(Instant.parse("2010-01-01T10:16:00Z"), userId, unitId, 500)
      )
      val journal: Seq[Event] = Seq(creationEvent) ++ deposits ++ withdrawals
      val initialAmount: Long = deposits.map(_.amountDeposited).sum - withdrawals.map(_.amountWithdrawn).sum
      val unitInfo: UnitInfo = UnitInfo(unitId, unitType, maximumAmount, initialAmount)
      val totalConsumed = withdrawals.map(_.amountWithdrawn).sum
      val totalGenerated = deposits.map(_.amountDeposited).sum
      val stats = UnitStats(unitId, unitType, totalConsumed, totalGenerated)
    }

    val units = Seq(justCreatedUnit, unitWithOperations)
    val statsByUnitType = units.groupBy(_.unitType).mapValues(values => {
      val totalConsumed = values.map(_.totalConsumed).sum
      val totalGenerated = values.map(_.totalGenerated).sum
      val count = values.length
      GroupedStats(totalConsumed, totalGenerated, count)
    })
    val globalStat = {
      val totalConsumed = units.map(_.totalConsumed).sum
      val totalGenerated = units.map(_.totalGenerated).sum
      val count = units.length
      GroupedStats(totalConsumed, totalGenerated, count)
    }
    val stats = UserStats(globalStat, statsByUnitType, units.map(_.stats))
    val usernameJournal: Seq[Event] = Seq(creationEvent)
    val userIdJournal: Seq[Event] = usernameJournal ++ units.flatMap(_.journal)
  }

  val users = Seq(userWithUnits, userWithoutUnits, anotherUserWithoutUnits, anotherUserWithUnits)
  val globalJournal = users.flatMap(_.userIdJournal)

  val specialValues = new {
    val emptyUserStats = UserStats(GroupedStats(0, 0, 0), Map.empty, List.empty)
  }
}
