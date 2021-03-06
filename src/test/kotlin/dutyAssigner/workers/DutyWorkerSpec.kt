package dutyAssigner.workers


import TimeHelper
import com.nhaarman.mockito_kotlin.*
import dutyAssigner.Event
import dutyAssigner.ICalendar
import flowdock.IFlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Thread
import flowdock.model.UpdateAction
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.koin.dsl.module.applicationContext
import org.koin.spek.KoinSpek
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import java.time.Instant
import java.time.LocalDate


val testKoinModule = applicationContext {
    bean { mock<ICalendar>() }
    bean { mock <IFlowdockAPI>() }
}

class DutyWorkerSpec : KoinSpek({
    beforeEachTest {
        closeKoin()
        startKoin(listOf(testKoinModule))
    }

    var timeHelper = TimeHelper()

    beforeEachTest {
        timeHelper = TimeHelper(LocalDate.parse("2018-04-03"))
    }

    describe("perform") {
        it("fetches events for correct time frames") {
            val calendar: ICalendar by inject()

            val worker = DutyWorker(
                weeksForward = 2,
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
            val calendar: ICalendar by inject()

            whenever(calendar.events(any(), any())).thenReturn(listOf(
                Event(
                    "1",
                    Instant.parse("2018-04-02T09:00:00Z"),
                    Instant.parse("2018-04-02T09:00:00Z"),
                    "Oskari",
                    "Investigator: X"
                ),
                Event(
                    "2",
                    Instant.parse("2018-04-03T09:00:00Z"),
                    Instant.parse("2018-04-03T09:00:00Z"),
                    "Sampo",
                    "Investigator: Sampo"
                ),
                Event(
                    "3",
                    Instant.parse("2018-04-03T09:00:00Z"),
                    Instant.parse("2018-04-03T09:00:00Z"),
                    "Valtteri",
                    "Tech Support Duty: X"
                )
            ))

            val flowdockAPI: IFlowdockAPI by inject()

            val worker = DutyWorker(
                weeksForward = 1,
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
                                urlTemplate = "http://duty.ngrok.io/book/1",
                                httpMethod = "POST"
                            )
                        ),
                        UpdateAction(
                            name = "Book 2018-04-03 Tech Support Duty: X",
                            target = UpdateAction.Target(
                                urlTemplate = "http://duty.ngrok.io/book/3",
                                httpMethod = "POST"
                            )
                        )
                    )
                )
            )))
        }
    }
})
