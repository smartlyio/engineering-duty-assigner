package dutyAssigner

import flowdock.IFlowdockAPI
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

const val WEEKS_FORWARD = 2;
class DutyWorker(val calendar: ICalendar, val flowdockAPI: IFlowdockAPI) {
    var now = { LocalDate.now() }

    constructor(
        calendar: ICalendar,
        flowdockAPI: IFlowdockAPI,
        now: () -> LocalDate
    ): this(calendar, flowdockAPI) {
        this.now = now
    }

    fun perform() {
        val startTimes = (0..WEEKS_FORWARD).map { i ->
            now().plusDays(7L * i) .with(DayOfWeek.MONDAY)
        }

        startTimes.forEach(::performForWeek)
    }

    private fun performForWeek(start: LocalDate) {
        val end = start.plusDays(6)
        val events = calendar.events(start, end)

        val flowdockEvents = createFlowdockActions(events)
        flowdockAPI.execute(flowdockEvents)
    }

    private fun createFlowdockActions(events: List<Event>): List<flowdock.Action> =
        listOf()
}