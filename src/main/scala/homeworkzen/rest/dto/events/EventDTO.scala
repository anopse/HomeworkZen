package homeworkzen.rest.dto.events

import java.time.Instant

trait EventDTO {
  def eventId: String

  def time: Instant
}
