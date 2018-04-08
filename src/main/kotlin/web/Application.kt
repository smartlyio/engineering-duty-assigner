package web

import dutyAssigner.ICalendar
import flowdock.IFlowdockAPI
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.coroutines.experimental.async
import org.koin.ktor.ext.inject

fun Application.dutyAssigner() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter())
    }

    routing {
        // TODO: Missing JWT signing validation
        post("book/{eventId}") {
            val updateAction = call.receive<UpdateAction>()

            async {
                // Book and kick an update to thread
                val calendar: ICalendar by inject()
                val flowdockAPI: IFlowdockAPI by inject()

                val eventId = call.parameters["eventId"]
                if (eventId !== null) {
                    val event = calendar.event(eventId)
                    val bookedEvent = event.book(updateAction.agent.name)

                    calendar.updateEvent(bookedEvent)
                    //flowdockAPI.createActivity()
                }
            }.await()

            call.respondText { "OK" }
        }
    }
}