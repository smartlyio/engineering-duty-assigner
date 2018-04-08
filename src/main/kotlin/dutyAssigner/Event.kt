package dutyAssigner

import java.time.Instant

data class Event(
    val id: String,
    val start: Instant,
    val end: Instant,
    val assignee: String,
    val description: String
) {
    fun book(newAssignee: String): Event {
        return this.copy(assignee = newAssignee, description = "${getDescriptionPrefix()}: $newAssignee" )
    }

    private fun getDescriptionPrefix(): String? =
        Regex("(.+):\\s(.+)").find(description)?.groups?.get(1)?.value
}