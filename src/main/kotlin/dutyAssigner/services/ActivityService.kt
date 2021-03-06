package dutyAssigner.services

import dutyAssigner.Event
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Thread
import flowdock.model.UpdateAction
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val HOST = "http://duty.ngrok.io" // TODO: Separate dev / test / prod

object ActivityService {
    fun createActivityFromEvents(title: String, author: Author, start: LocalDate, events: List<Event>): Activity {
        return Activity(
            title = title,
            author = author,
            external_thread_id = start.format(DateTimeFormatter.ISO_DATE),
            thread = Thread(
                title = "Support duties for ${start.format(DateTimeFormatter.ISO_DATE)}",
                status = formatStatus(events),
                actions = events.map { event ->
                    UpdateAction(
                        name = "Book ${LocalDate.ofInstant(event.start, ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE) } ${event.description}",
                        target = UpdateAction.Target(
                            urlTemplate = formatUrlTemplate(event),
                            httpMethod = "POST"
                        )
                    )
                })
        )
    }

    private fun formatStatus(events: List<Event>): Thread.Status {
        if (events.isEmpty()) {
            return Thread.Status("All booked", "green")
        } else {
            return Thread.Status("${events.size} duties missing", "red")
        }
    }

    private fun formatUrlTemplate(event: Event): String {
        return "${HOST}/book/${event.id}"
    }
}