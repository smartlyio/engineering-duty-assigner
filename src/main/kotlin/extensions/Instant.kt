package extensions

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate

fun Instant.startOfWeek(): LocalDate {
    val localDate = this.atZone(java.time.ZoneOffset.UTC).toLocalDate()
    return localDate.with(DayOfWeek.MONDAY)
}