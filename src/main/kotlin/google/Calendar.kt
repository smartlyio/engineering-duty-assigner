package google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.util.DateTime
import dutyAssigner.Event
import dutyAssigner.ICalendar
import kotlinx.coroutines.experimental.async
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.api.services.calendar.Calendar as GoogleCalendar
import com.google.api.services.calendar.model.Event as GoogleEvent

const val CALENDAR_ID = "9n92ukjvquobse8k6efup7jalk@group.calendar.google.com" // Extract me to somewhere else

class Calendar(val credential: Credential) : ICalendar {
    override fun events(start: LocalDate, end: LocalDate): List<Event> {
        val service = service()
        return service.events().list(CALENDAR_ID)
            .setTimeMin(DateTime(start.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)))
            .setTimeMax(DateTime(end.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)))
            .setSingleEvents(true)
            .execute()
            .items
            .map(::toDutyAssignerEvent)

    }

    override fun book(assignee: String, eventId: String) {
        val service = service()
        val event = service.events().get(CALENDAR_ID, eventId).execute()
        event.summary = "UPDATE ME TO SOMETHING MEANINGFUL"
        service.events().update("foo", "bar", event).execute()
    }

    private fun service(): GoogleCalendar {
        return GoogleCalendar.Builder(
            Factories.HTTP_TRANSPORT, Factories.JSON_FACTORY, credential)
            .setApplicationName("Duty Assigner")
            .build()
    }

    private fun toDutyAssignerEvent(googleEvent: GoogleEvent): Event {
        return Event(
            id = googleEvent.id,
            start = Instant.ofEpochMilli(googleEvent.start.dateTime.value),
            end = Instant.ofEpochMilli(googleEvent.end.dateTime.value),
            assignee = "Oskari", // TODO
            description = googleEvent.summary
        )
    }
}