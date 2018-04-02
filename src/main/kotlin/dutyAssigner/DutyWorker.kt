package dutyAssigner

import flowdock.Action
import flowdock.CreateActivity
import flowdock.IFlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Thread
import flowdock.model.UpdateAction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

        val flowdockEvents = createFlowdockActions(start, events)
        flowdockAPI.execute(flowdockEvents)
    }

    private fun createFlowdockActions(start: LocalDate, events: List<Event>): List<Action> {
        val unassignedDuties = events.filter { it.description matches Regex(".+: X$") }

        if (unassignedDuties.isEmpty()) {
            return listOf()
        } else {
            return listOf(
                CreateActivity(Activity(
                    title = "Updated thread",
                    author = Author(name = "Bob"),
                    external_thread_id = start.format(DateTimeFormatter.ISO_DATE),
                    thread = Thread(
                        title = "Support duties for ${start.format(DateTimeFormatter.ISO_DATE)}",
                        status = Thread.Status("2 missing", "red"),
                        actions = unassignedDuties.map { event ->
                            UpdateAction(
                                name = "Book ${LocalDate.ofInstant(event.start, ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE) } ${event.description}",
                                target = UpdateAction.Target(
                                    urlTemplate = "http://www.example.com",
                                    httpMethod = "POST"
                                )
                            )
                        })
                    )
                )
            )
        }
    }
}