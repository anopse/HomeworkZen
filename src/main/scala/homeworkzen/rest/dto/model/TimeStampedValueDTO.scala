package homeworkzen.rest.dto.model

import java.time.Instant

case class TimeStampedValueDTO(time: Instant,
                               amount: Long)