import com.github.kittinunf.fuel.core.FuelManager
import dutyAssigner.workers.DutyWorker
import dutyAssigner.ICalendar
import flowdock.FlowdockAPI
import flowdock.IFlowdockAPI
import google.Authorization
import google.Calendar
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import web.dutyAssigner
import java.time.Duration
import kotlin.system.exitProcess


val KoinModule = applicationContext {
    factory { Authorization.authorize() }
    bean { Calendar(get()) as ICalendar }
    bean { FlowdockAPI(getProperty("FLOW_TOKEN")) as IFlowdockAPI }
}

fun main(args: Array<String>) {
    FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json")
    StandAloneContext.startKoin(listOf(KoinModule), useEnvironmentProperties = true)

    val errorHandler = { e: Throwable ->
        println("Got error ${e.message}. Shutting down")
        exitProcess(-1)
    }

    val dutyWorker = DutyWorker()

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