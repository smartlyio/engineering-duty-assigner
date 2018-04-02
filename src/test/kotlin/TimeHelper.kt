import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class TimeHelper(val time: Instant = Instant.now()) {
    constructor(time: LocalDate) : this(
        time.atStartOfDay().toInstant(ZoneOffset.UTC)
    )

    private var modification = Duration.ofSeconds(0)

    fun nowInstant(): Instant {
        return time.plus(modification)
    }

    fun nowLocalDate(): LocalDate {
        return LocalDate.ofInstant(time, ZoneOffset.UTC)
    }

    fun forward(duration: Duration) {
        modification += duration
    }

    fun backward(duration: Duration) {
        modification -= duration
    }
}