import com.kizitonwose.time.Interval
import com.kizitonwose.time.TimeUnit
import com.kizitonwose.time.plus
import com.kizitonwose.time.seconds
import java.util.*

class TimeHelper {
    private val time = Calendar.getInstance()
    private var modification = 0.seconds

    fun now(): Calendar {
        return time + modification
    }

    fun forward(time: Interval<TimeUnit>) {
        modification += time
    }

    fun backward(time: Interval<TimeUnit>) {
        modification -= time
    }
}