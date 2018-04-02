package dutyAssigner


import TimeHelper
import com.nhaarman.mockito_kotlin.*
import flowdock.IFlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Thread
import flowdock.model.UpdateAction
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.time.Instant
import java.time.LocalDate

class DutyWorkerSpec : Spek({
    var timeHelper = TimeHelper()

    beforeEachTest {
        timeHelper = TimeHelper(LocalDate.parse("2018-04-03"))
    }

    describe("perform") {
        it("fetches events for correct time frames") {
            val calendar = mock<ICalendar>()
            val flowdockAPI = mock<IFlowdockAPI>()

            val worker = DutyWorker(
                weeksForward = 2,
                calendar = calendar,
                flowdockAPI = flowdockAPI,
                now = timeHelper::nowLocalDate
            )

            worker.perform()

            verify(calendar).events(
                eq(LocalDate.parse("2018-04-02")),
                eq(LocalDate.parse("2018-04-08"))
            )

            verify(calendar).events(
                eq(LocalDate.parse("2018-04-09")),
                eq(LocalDate.parse("2018-04-15"))
            )
        }

        it("sends Flowdock activity message from new state") {
            val calendar = mock<ICalendar> {
                on { events(any(), any()) } doReturn listOf(
                    Event(
                        Instant.parse("2018-04-02T09:00:00Z"),
                        Instant.parse("2018-04-02T09:00:00Z"),
                        "Oskari",
                        "Investigator: X"
                    ),
                    Event(
                        Instant.parse("2018-04-03T09:00:00Z"),
                        Instant.parse("2018-04-03T09:00:00Z"),
                        "Sampo",
                        "Investigator: Sampo"
                    ),
                    Event(
                        Instant.parse("2018-04-03T09:00:00Z"),
                        Instant.parse("2018-04-03T09:00:00Z"),
                        "Valtteri",
                        "Tech Support Duty: X"
                    )
                )
            }

            val flowdockAPI = mock<IFlowdockAPI>()

            val worker = DutyWorker(
                weeksForward = 1,
                calendar = calendar,
                flowdockAPI = flowdockAPI,
                now = timeHelper::nowLocalDate
            )

            worker.perform()

            verify(flowdockAPI).createActivity(eq(Activity(
                title = "Updated thread",
                author = Author(name = "Bob"),
                external_thread_id = "2018-04-02",
                thread = flowdock.model.Thread(
                    title = "Support duties for 2018-04-02",
                    status = Thread.Status("2 duties missing", "red"),
                    actions = listOf(
                        UpdateAction(
                            name = "Book 2018-04-02 Investigator: X",
                            target = UpdateAction.Target(
                                urlTemplate = "http://www.example.com",
                                httpMethod = "POST"
                            )
                        ),
                        UpdateAction(
                            name = "Book 2018-04-03 Tech Support Duty: X",
                            target = UpdateAction.Target(
                                urlTemplate = "http://www.example.com",
                                httpMethod = "POST"
                            )
                        )
                    )
                )
            )))
        }
    }
})