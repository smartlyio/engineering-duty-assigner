import com.github.kittinunf.fuel.core.FuelManager
import dutyAssigner.DutyWorker
import flowdock.FlowdockAPI
import google.Authorization
import google.Calendar
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import web.dutyAssigner
import java.time.Duration
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json")

    val errorHandler = { e: Throwable ->
        println("Got error ${e.message}. Shutting down")
        exitProcess(-1)
    }

    val credential = Authorization.authorize()
    val calendar = Calendar(credential)
    val dutyWorker = DutyWorker(calendar = calendar, flowdockAPI =  FlowdockAPI(System.getenv("FLOW_TOKEN")))

    val job = PeriodicJob(
        runEvery = Duration.ofMinutes(1),
        job = dutyWorker::perform,
        errorHandler = errorHandler
    )

    job.start()


    val server = embeddedServer(
        Netty,
        port = 8080,
        watchPaths = listOf("src/main/kotlin"),
        module = Application::dutyAssigner
    )

    server.start(wait = true)
}