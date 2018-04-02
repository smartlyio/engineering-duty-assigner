import java.lang.Thread.sleep
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread

typealias OnErrorHandler = (e: Throwable) -> Unit

class PeriodicJob(
    val runEvery: Duration,
    val job: () -> Unit,
    val errorHandler: OnErrorHandler = {}
) {
    var lastRun: Instant = Instant.ofEpochMilli(0)
    val thread = thread(start = false, block = ::execute)

    var now = { Instant.now() }

    init {
        thread.setUncaughtExceptionHandler { _, error -> this.errorHandler(error) }
    }

    /**
     * Used in specs to inject dependencies
     */
    constructor(
        runEvery: Duration,
        job: () -> Unit,
        errorHandler: OnErrorHandler = {},
        now: () -> Instant
    ): this(runEvery, job, errorHandler) {
        this.now = now;
    }

    fun runJob() {
        if (isExecuteTime()) {
            job()
            lastRun = now()
        }
    }

    fun start() {
        thread.start()
    }

    fun stop() {
        thread.interrupt()
    }

    private fun execute() {
        while(true) {
            try {
                runJob()
                sleep(Duration.ofSeconds(1).toMillis())
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    private fun isExecuteTime(): Boolean {
        return lastRun + runEvery < now()
    }
}