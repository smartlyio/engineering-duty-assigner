import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.time.Duration

fun retry(times: Int = 10, block: () -> Unit) {
    try {
        block()
    } catch(e: Throwable) {
        if (times > 0) {
            retry(times  - 1, block)
        } else {
            throw e
        }
    }
}

class PeriodicJobSpec : Spek({
    var timeHelper = TimeHelper()

    beforeEachTest {
        timeHelper = TimeHelper()
    }

    describe("runJob") {
        it("schedules jobs on a given interval") {
            val job: () -> Unit = mock()

            val periodicJob = PeriodicJob(runEvery = Duration.ofMinutes(1), job = job, now = timeHelper::nowInstant)

            periodicJob.runJob()
            timeHelper.forward(Duration.ofSeconds(90))
            periodicJob.runJob()

            verify(job, times(2)).invoke()
        }

        it("runs a job only once on a given bucket") {
            val job: () -> Unit = mock()

            val periodicJob = PeriodicJob(runEvery = Duration.ofMinutes(1), job = job, now = timeHelper::nowInstant)
            periodicJob.runJob()
            periodicJob.runJob()

            verify(job).invoke()
        }
    }

    describe("error handling") {
        it("calls error handler on error in job") {
            val job = { throw Exception("terrible error") }
            val errorHandler: (e: Throwable) -> Unit = mock()

            val periodicJob = PeriodicJob(
                runEvery = Duration.ofMinutes(1), job = job, errorHandler = errorHandler, now = timeHelper::nowInstant
            )

            try {
                periodicJob.start()
                retry { verify(errorHandler).invoke(argThat { this.message == "terrible error" }) }
            } finally {
                periodicJob.stop()
            }
        }
    }
})