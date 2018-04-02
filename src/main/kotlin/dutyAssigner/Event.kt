package dutyAssigner

import java.time.Instant

data class Event(
    val start: Instant,
    val end: Instant,
    val assignee: String,
    val description: String
)