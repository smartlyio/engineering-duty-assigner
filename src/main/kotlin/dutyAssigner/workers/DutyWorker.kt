package dutyAssigner.workers

import dutyAssigner.Event
import dutyAssigner.ICalendar
import dutyAssigner.services.ActivityService
import flowdock.IFlowdockAPI
import flowdock.model.Activity
import java.time.DayOfWeek
import java.time.LocalDate
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class DutyWorker(val weeksForward: Int = 2) : KoinComponent {
    var now = { LocalDate.now() }
    val calendar: ICalendar by inject()
    val flowdockAPI: IFlowdockAPI by inject()

    constructor(
        weeksForward: Int,
        now: () -> LocalDate
    ): this(weeksForward) {
        this.now = now
    }

    fun perform() {
        val startTimes = (0 until weeksForward).map { i ->
            now().plusDays(7L * i) .with(DayOfWeek.MONDAY)
        }

        startTimes.forEach(::performForWeek)
    }

    private fun performForWeek(start: LocalDate) {
        val end = start.plusDays(6)
        val events = calendar.events(start, end)

        val activity = createFlowdockActivity(start, events)
        if (activity != null) {
            flowdockAPI.createActivity(activity)
        }
    }

    private fun createFlowdockActivity(start: LocalDate, events: List<Event>): Activity? {
        val unassignedDuties = events.filter { it.description matches Regex(".+: X$") }

        if (unassignedDuties.isEmpty()) {
            return null
        } else {
            return ActivityService.createActivityFromEvents(start, unassignedDuties)
        }
    }
}