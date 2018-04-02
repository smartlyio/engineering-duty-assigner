import dutyAssigner.WeeklyDuties
import google.Authorization
import google.Calendar
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.time.Duration
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    WeeklyDuties().update()
    exitProcess(-1)
    val errorHandler = { e: Throwable ->
        println("Got error ${e.message}. Shutting down")
        exitProcess(-1)
    }

    val job = PeriodicJob(
        runEvery= Duration.ofHours(1),
        job = {},
        errorHandler = errorHandler
    )

    job.start()

    val credential = Authorization.authorize()
    val events = Calendar(credential).events(42)

    val server = embeddedServer(
        Netty,
        port = 8080,
        watchPaths = listOf("src/main/kotlin"),
        module = Application::dutyAssigner
    )

    server.start(wait = true)
}