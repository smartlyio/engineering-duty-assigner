import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.*

fun Application.dutyAssigner() {
    routing {
        get("foo") {
            call.respondText("And from separate module")
        }
    }
}