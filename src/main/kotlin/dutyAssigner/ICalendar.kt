package dutyAssigner

import java.time.LocalDate

interface ICalendar {
    fun event(eventId: String): Event
    fun events(start: LocalDate, end: LocalDate): List<Event>
    fun updateEvent(event: Event)
}