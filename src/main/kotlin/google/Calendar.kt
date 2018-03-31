package google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.Calendar as GoogleCalendar

import dutyAssigner.ICalendar

class Calendar(val credential: Credential) : ICalendar {
    fun events(): List<Event> {
        val service = service()
        return service.events().list("9n92ukjvquobse8k6efup7jalk@group.calendar.google.com")
            .execute()
            .items
    }

    private fun service(): GoogleCalendar {
        return GoogleCalendar.Builder(
            Factories.HTTP_TRANSPORT, Factories.JSON_FACTORY, credential)
            .setApplicationName("Duty Assigner")
            .build()
    }
}