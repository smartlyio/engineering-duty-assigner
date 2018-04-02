package google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.util.DateTime
import dutyAssigner.Event
import com.google.api.services.calendar.model.Event as GoogleEvent
import com.google.api.services.calendar.Calendar as GoogleCalendar

import dutyAssigner.ICalendar
import io.ktor.util.toGMT
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Calendar(val credential: Credential) : ICalendar {
    override fun events(start: LocalDate, end: LocalDate): List<Event> {
        val service = service()
        return service.events().list("9n92ukjvquobse8k6efup7jalk@group.calendar.google.com")
            .setTimeMin(DateTime(start.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)))
            .setTimeMax(DateTime(end.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)))
            .setSingleEvents(true)
            .execute()
            .items
            .map(::toDutyAssignerEvent)

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