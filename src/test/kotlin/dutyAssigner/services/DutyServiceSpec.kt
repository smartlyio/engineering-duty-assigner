package dutyAssigner.services


import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import dutyAssigner.Event
import dutyAssigner.ICalendar
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.koin.dsl.module.applicationContext
import org.koin.spek.KoinSpek
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import java.time.Instant
import java.time.LocalDate

val testKoinModule = applicationContext {
    bean { mock<ICalendar>() }
}
class DutyServiceSpec : KoinSpek({
    beforeEachTest {
        StandAloneContext.closeKoin()
        StandAloneContext.startKoin(listOf(testKoinModule))
    }

    describe("#eventsForWeek") {
        it("returns events for the following six days") {
            DutyService.eventsForWeek(LocalDate.parse("2018-04-09"))

            val calendar: ICalendar by inject()
            verify(calendar).events(LocalDate.parse("2018-04-09"), LocalDate.parse("2018-04-15"))
        }
    }

    describe("#filterUnassignedDuties") {
        it("filters duties marked with X") {
            val assignedDuty = Event(
                id = "123",
                assignee = "Oskari",
                description = "Investigator: Oskari",
                start = Instant.parse("2018-04-02T09:00:00Z"),
                end = Instant.parse("2018-04-02T17:00:00Z")
            )

            val unassignedInvestigatorDuty = Event(
                id = "124",
                assignee = "X",
                description = "Investigator: X",
                start = Instant.parse("2018-04-03T09:00:00Z"),
                end = Instant.parse("2018-04-03T17:00:00Z")
            )

            val unassignedTechSupportDuty = Event(
                id = "124",
                assignee = "X",
                description = "Tech Support Duty: X",
                start = Instant.parse("2018-04-03T16:00:00Z"),
                end = Instant.parse("2018-04-03T20:00:00Z")
            )

            val duties = listOf(assignedDuty, unassignedInvestigatorDuty, unassignedTechSupportDuty)
            val unassignedDuties = listOf(unassignedInvestigatorDuty, unassignedTechSupportDuty)
            DutyService.filterUnassignedDuties(duties) shouldEqual unassignedDuties
        }
    }
})