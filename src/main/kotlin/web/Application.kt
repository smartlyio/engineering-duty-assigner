package web

import google.Authorization
import google.Calendar
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

fun Application.dutyAssigner() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter())
    }

    routing {
        // TODO: Missing JWT signing validation
        post("book/{eventId}") {
            val updateAction = call.receive<UpdateAction>()

            val credential = Authorization.authorize()
            val calendar = Calendar(credential)

            async {
                // Book and kick an update to thread
                // calendar.book(updateAction.agent.name, call)
            }.await()

            call.respondText { "OK" }
        }
    }
}