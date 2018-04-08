package extensions


import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.time.Instant
import java.time.LocalDate

class InstantSpec : Spek({
    describe("#startOfWeek") {
        it("returns LocalDate at start of week") {
            Instant.parse("2018-04-06T09:00:00Z").startOfWeek() shouldEqual LocalDate.parse("2018-04-02")
            Instant.parse("2018-04-02T17:51:00Z").startOfWeek() shouldEqual LocalDate.parse("2018-04-02")
            Instant.parse("2018-04-08T23:59:59Z").startOfWeek() shouldEqual LocalDate.parse("2018-04-02")
        }
    }
})