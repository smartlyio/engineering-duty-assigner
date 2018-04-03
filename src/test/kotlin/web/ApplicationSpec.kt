package web


import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ApplicationSpec : Spek({
    describe("book/{eventId}") {
        it("does something") {
            "foo" shouldEqual "bar"
        }
    }
})