package homeworkzen.rest.dto

import homeworkzen.domain.message.CreateUserCommand
import homeworkzen.util.Hasher

case class RegisterUserRequestDTO(username: String, password: String) {
  def toCommand: CreateUserCommand = CreateUserCommand(username, Hasher(password))
}