import google.Authorization
import google.Calendar
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    val credential = Authorization.authorize()
    val events = Calendar(credential).events()

    val server = embeddedServer(
        Netty,
        port = 8080,
        watchPaths = listOf("src/main/kotlin"),
        module = Application::dutyAssigner
    )

    server.start(wait = true)
}