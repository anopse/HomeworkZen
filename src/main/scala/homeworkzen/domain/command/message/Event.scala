package homeworkzen.domain.command.message

import java.time.Instant

trait Event {
  def timestamp: Instant
}
