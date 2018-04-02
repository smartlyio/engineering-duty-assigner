package web

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.*

data class Foo(val foo: String)

fun Application.dutyAssigner() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter())
    }

    routing {
        post("foo") {
            val foo = call.receive<Foo>()
            println(foo.foo)
            call.respondText { "and from separate module" }
        }
    }
}