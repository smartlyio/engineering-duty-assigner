import com.kizitonwose.time.minutes
import com.kizitonwose.time.seconds
import com.nhaarman.mockito_kotlin.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

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

            val periodicJob = PeriodicJob(runEvery = 1.minutes, job = job, now = timeHelper::now)

            periodicJob.runJob()
            timeHelper.forward(90.seconds)
            periodicJob.runJob()
            verify(job, times(2)).invoke()
        }

        it("runs a job only once on a given bucket") {
            val job: () -> Unit = mock()

            val periodicJob = PeriodicJob(runEvery = 1.minutes, job = job, now = timeHelper::now)
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
                runEvery = 1.minutes, job = job, errorHandler = errorHandler, now = timeHelper::now
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