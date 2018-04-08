package dutyAssigner


import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.time.Instant

class EventSpec : Spek({
    describe("#book") {
        val event = Event(
            id = "1",
            assignee = "X",
            description = "Investigator: X",
            start = Instant.parse("2018-02-02T09:00:00Z"),
            end = Instant.parse("2018-02-02T17:00:00Z")

        )

        val bookedEvent = event.book("Oskari")

        it("returns new event with the new assignee") {
            bookedEvent.assignee shouldEqual "Oskari"
        }

        it("returns new event with the new description") {
            bookedEvent.description shouldEqual "Investigator: Oskari"
        }
    }
})