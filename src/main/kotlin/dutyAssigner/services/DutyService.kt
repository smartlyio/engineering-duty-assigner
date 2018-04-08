package dutyAssigner.services

import dutyAssigner.Event
import dutyAssigner.ICalendar
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.time.LocalDate

object DutyService: KoinComponent {
    fun eventsForWeek(start: LocalDate): List<Event> {
        val calendar: ICalendar by inject()

        val end = start.plusDays(6)
        return calendar.events(start, end)
    }

}