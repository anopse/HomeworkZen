package homeworkzen.rest.dto

import java.time.Instant

case class TimeStampedValueDTO(time: Instant,
                               amount: Long)