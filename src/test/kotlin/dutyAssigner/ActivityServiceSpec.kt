package dutyAssigner


import flowdock.model.Thread
import flowdock.model.UpdateAction
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.time.Instant
import java.time.LocalDate

class ActivityServiceSpec : Spek({
    describe("createActivityFromEvents") {
        it("contains basic info") {
            val activity = ActivityService.createActivityFromEvents(LocalDate.parse("2018-04-02"), emptyList())
            activity.title shouldEqual "Updated thread"
            activity.author.name shouldEqual "Bob"
            activity.thread.title shouldEqual "Support duties for 2018-04-02"
            activity.thread.status!! shouldEqual Thread.Status("All booked", "green")
        }

        it("has amount of missing duties in status") {
            val events = listOf(
                Event("1", Instant.parse("2018-04-02T09:00:00.00Z"), Instant.parse("2018-04-02T17:00:00.00Z"), "Oskari", "Investigator: X")
            )
            val activity = ActivityService.createActivityFromEvents(LocalDate.parse("2018-04-02"), events)
            activity.thread.status!! shouldEqual Thread.Status("1 duties missing", "red")
        }

        it("creates assign events for missing duties") {
            val events = listOf(
                Event("1", Instant.parse("2018-04-02T09:00:00.00Z"), Instant.parse("2018-04-02T17:00:00.00Z"), "Oskari", "Investigator: X")
            )
            val activity = ActivityService.createActivityFromEvents(LocalDate.parse("2018-04-02"), events)
            activity.thread.actions!! shouldEqual listOf(
                UpdateAction("Book 2018-04-02 Investigator: X", UpdateAction.Target("http://duty.ngrok.io/book/1", "POST"))
            )
        }
    }
})