package dutyAssigner

import java.time.Instant

data class Event(
    val id: String,
    val start: Instant,
    val end: Instant,
    val assignee: String,
    val description: String
)