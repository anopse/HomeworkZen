package homeworkzen.util

import java.nio.charset.StandardCharsets

object Base64 {
  private val encoder = java.util.Base64.getEncoder
  private val decoder = java.util.Base64.getDecoder
  private val charset = StandardCharsets.UTF_8

  def encodeString(string: String): String = encoder.encodeToString(string.getBytes(charset))

  def decodeString(string: String): String = new String(decoder.decode(string), charset.name)
}
