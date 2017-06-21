package homeworkzen.util

import java.math.BigInteger
import java.security.MessageDigest

import homeworkzen.Config


object Hasher {
  def apply(passwordToHash: String): String = {
    val salt = Config.Api.hashSalt
    val md = MessageDigest.getInstance("SHA-512")
    md.update(salt.getBytes("UTF-8"))
    val bytes = md.digest(passwordToHash.getBytes("UTF-8"))
    val digest = new BigInteger(1, bytes)
    f"$digest%064x"
  }
}
