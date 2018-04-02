import com.kizitonwose.time.*
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.thread

typealias OnErrorHandler = (e: Throwable) -> Unit

class PeriodicJob(
    val runEvery: Interval<TimeUnit>,
    val job: () -> Unit,
    val errorHandler: OnErrorHandler = {}
) {
    var lastRun: Calendar = Calendar.Builder().setDate(0, 0, 0).build()
    val thread = thread(start = false, block = ::execute)

    var now = { Calendar.getInstance() }

    init {
        thread.setUncaughtExceptionHandler { _, error -> this.errorHandler(error) }
    }

    /**
     * Used in specs to inject dependencies
     */
    constructor(
        runEvery: Interval<TimeUnit>,
        job: () -> Unit,
        errorHandler: OnErrorHandler = {},
        now: () -> Calendar
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
                sleep(1.seconds.inMilliseconds.longValue)
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    private fun isExecuteTime(): Boolean {
        return lastRun + runEvery < now()
    }
}