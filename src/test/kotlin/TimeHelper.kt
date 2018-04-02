import java.time.Duration
import java.time.Instant

class TimeHelper {
    private val time = Instant.now()
    private var modification = Duration.ofSeconds(0)

    fun now(): Instant {
        return time.plus(modification)
    }

    fun forward(duration: Duration) {
        modification += duration
    }

    fun backward(duration: Duration) {
        modification -= duration
    }
}