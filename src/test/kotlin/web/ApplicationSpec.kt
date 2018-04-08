package web


import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import dutyAssigner.Event
import dutyAssigner.ICalendar
import flowdock.IFlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Thread
import flowdock.model.UpdateAction
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.koin.dsl.module.applicationContext
import org.koin.spek.KoinSpek
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import java.time.Instant

val testKoinModule = applicationContext {
    bean { mock<ICalendar>() }
    bean { mock<IFlowdockAPI>() }
}

class ApplicationSpec : KoinSpek({
    StandAloneContext.startKoin(listOf(testKoinModule))

    val engine = TestApplicationEngine(createTestEnvironment())
    engine.start(wait = false)
    engine.application.dutyAssigner()

    with(engine) {
        describe("book/{eventId}") {
            val updateAction = """
                {
                  "@type": "UpdateAction",
                  "name": "Book 2018-04-09 Investigator: X",
                  "agent": {
                    "@type": "Person",
                    "name": "Oskari",
                    "url": "https://api.flowdock.com/users/33816",
                    "image": "https://d2cxspbh1aoie1.cloudfront.net/avatars/97456b60d8655e12d0922c1afb88f5ca/120"
                  },
                  "target": {
                    "@type": "EntryPoint",
                    "urlTemplate": "http://duty.ngrok.io/book/123",
                    "httpMethod": "POST"
                  }
                }
                """

            val calendar: ICalendar by inject()
            whenever(calendar.event("123")).thenReturn(
                Event(
                    id = "123",
                    start = Instant.parse("2018-04-09T09:00:00Z"),
                    end = Instant.parse("2018-04-09T17:00:00Z"),
                    description = "Investigator: 'X",
                    assignee =  "X"
                )
            )

            handleRequest(HttpMethod.Post, "book/123", { this.body = updateAction }).let { call ->
                call.response.awaitCompletion()

                it("returns 200") {
                    call.response.status()!! shouldEqual HttpStatusCode.OK
                }

                it("books a duty by changing the calendar event") {
                    verify(calendar).updateEvent(eq(
                        Event(
                            id = "123",
                            start = Instant.parse("2018-04-09T09:00:00Z"),
                            end = Instant.parse("2018-04-09T17:00:00Z"),
                            description = "Investigator: Oskari",
                            assignee =  "Oskari"
                        )
                    ))
                }

                it("sends a notification to Flowdock thread") {
                    val flowdockAPI: IFlowdockAPI by inject()
                    verify(flowdockAPI).createActivity(eq(
                        Activity(
                            title = "booked an event: Investigator: Oskari",
                            author = Author(name = "Oskari", avatar="https://d2cxspbh1aoie1.cloudfront.net/avatars/97456b60d8655e12d0922c1afb88f5ca/120"),
                            external_thread_id = "2018-04-09",
                            thread = flowdock.model.Thread(
                                title = "Support duties for 2018-04-09",
                                status = Thread.Status("All booked", "green"),
                                actions = listOf()
                            )
                        )
                    ))
                }
            }
        }
    }
})