package dutyAssigner

import flowdock.IFlowdockAPI
import flowdock.model.Activity
import java.time.DayOfWeek
import java.time.LocalDate

class DutyWorker(val weeksForward: Int = 2, val calendar: ICalendar, val flowdockAPI: IFlowdockAPI) {
    var now = { LocalDate.now() }

    constructor(
        calendar: ICalendar,
        flowdockAPI: IFlowdockAPI,
        weeksForward: Int,
        now: () -> LocalDate
    ): this(weeksForward, calendar, flowdockAPI) {
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
            return ActicityService.createActivityFromEvents(start, unassignedDuties)
        }
    }
}