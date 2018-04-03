package dutyAssigner

import java.time.LocalDate

interface ICalendar {
    fun events(start: LocalDate, end: LocalDate): List<Event>
    fun book(assignee: String, eventId: String)
}