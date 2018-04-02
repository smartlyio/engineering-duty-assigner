package dutyAssigner


import TimeHelper
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import flowdock.IFlowdockAPI
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.time.Instant
import java.time.LocalDate

class DutyWorkerSpec : Spek({
    var timeHelper = TimeHelper()

    beforeEachTest { timeHelper = TimeHelper(LocalDate.parse("2018-04-03")) }

    describe("perform") {
        val calendar: ICalendar = mock()
        val flowdockAPI : IFlowdockAPI = mock()

        val worker = DutyWorker(
            calendar = calendar,
            flowdockAPI = flowdockAPI,
            now = timeHelper::nowLocalDate
        )

        worker.perform()

        it("fetches events for correct time frames") {
            verify(calendar).events(
                eq(LocalDate.parse("2018-04-02")),
                eq(LocalDate.parse("2018-04-08"))
            )

            verify(calendar).events(
                eq(LocalDate.parse("2018-04-09")),
                eq(LocalDate.parse("2018-04-15"))
            )
        }
    }
})