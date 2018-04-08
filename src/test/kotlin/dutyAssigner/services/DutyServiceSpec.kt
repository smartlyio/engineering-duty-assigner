package dutyAssigner.services


import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import dutyAssigner.ICalendar
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.koin.dsl.module.applicationContext
import org.koin.spek.KoinSpek
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
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
})