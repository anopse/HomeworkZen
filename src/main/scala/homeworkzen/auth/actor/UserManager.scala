package homeworkzen.auth.actor

import java.util.UUID

import akka.actor.Actor
import homeworkzen.auth.Hasher
import homeworkzen.auth.message._
import homeworkzen.model._

import scala.collection.mutable

class UserManager extends Actor {

  // todo : persist user registration
  private val users: mutable.HashMap[String, UserEntry] = mutable.HashMap.empty

  override def receive: Receive = {
    case request: GetUserEntryRequest => handle(request)
    case request: UserRegistrationRequest => handle(request)
  }

  private def handle(request: GetUserEntryRequest): Unit = {
    val result = users.get(request.username)
      .map(Right(_): Either[GetUserEntryError, UserEntry])
      .getOrElse(Left(UsernameNotFound))
    sender ! GetUserEntryResult(request, result)
  }

  private def handle(request: UserRegistrationRequest): Unit = {
    // todo : optionnaly add password & username requirements
    val result = Either.cond(!users.contains(request.username), {
      val id = UserId(UUID.randomUUID())
      val newEntry = UserEntry(id, request.username, Hasher(request.password))
      users += request.username -> newEntry
      newEntry
    }, UsernameAlreadyExist)
    sender ! UserRegistrationResult(request, result)
  }
}
