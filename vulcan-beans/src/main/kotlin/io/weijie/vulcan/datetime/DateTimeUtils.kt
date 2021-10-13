@file:JvmName("DateTimeUtils")
package io.weijie.vulcan.datetime

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.NoSuchElementException

@JvmOverloads
fun Date.isToday(zone: ZoneId = ZoneId.systemDefault()) = this.localDate(zone).isEqual(LocalDate.now(zone))

@JvmOverloads
fun Date.isPastDay(zone: ZoneId = ZoneId.systemDefault()) = this.localDate(zone).isBefore(LocalDate.now(zone))

@JvmOverloads
fun Date.isFutureDay(zone: ZoneId = ZoneId.systemDefault()) = this.localDate(zone).isAfter(LocalDate.now(zone))

@JvmOverloads
fun todayStart(zone: ZoneId = ZoneId.systemDefault()): Date {
    val zoned = LocalDate.now().atStartOfDay(zone)
    return Date.from(Instant.from(zoned))
}

@JvmOverloads
fun todayEnd(zone: ZoneId = ZoneId.systemDefault()): Date {
    val zoned = LocalDate.now().plusDays(1).atStartOfDay(zone).minusNanos(1)
    return Date.from(Instant.from(zoned))
}

fun nowMillis() = Instant.now().toEpochMilli()

@JvmOverloads
fun lastDayOfWeek(zone: ZoneId = ZoneId.systemDefault()): Date {
    val today = LocalDate.now(zone)
    if (today.dayOfWeek == DayOfWeek.SUNDAY) {
        val endDay = today.plusDays(1).atStartOfDay(zone).minusNanos(1)
        return Date.from(Instant.from(endDay))
    }
    val zoned = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusDays(1).atStartOfDay(zone).minusNanos(1)
    return Date.from(Instant.from(zoned))
}

@JvmOverloads
fun nextDayOfWeek(dayOfWeek: DayOfWeek, zone: ZoneId = ZoneId.systemDefault()): LocalDate {
    return LocalDate.now(zone).with(TemporalAdjusters.next(dayOfWeek))
}

@JvmOverloads
fun Date.dayStart(zone: ZoneId = ZoneId.systemDefault()): Date {
    val localTime = this.localDate(zone).atStartOfDay(zone)
    return Date.from(Instant.from(localTime))
}

@JvmOverloads
fun Date.dayEnd(zone: ZoneId = ZoneId.systemDefault()): Date {
    val localTime = this.localDate(zone).plusDays(1).atStartOfDay(zone).minusNanos(1)
    return Date.from(Instant.from(localTime))
}

@JvmOverloads
fun LocalTime.date(localDate: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Date {
    val instant = Instant.from(localDate.atTime(this).atZone(zone))
    return Date.from(instant)
}

@JvmOverloads
fun LocalDate.date(zone: ZoneId = ZoneId.systemDefault()): Date {
    val instant = Instant.from(this.atStartOfDay(zone))
    return Date.from(instant)
}

@JvmOverloads
fun LocalDateTime.date(zone: ZoneId = ZoneId.systemDefault()): Date {
    val instant = Instant.from(this.atZone(zone))
    return Date.from(instant)
}

@JvmOverloads
fun Long.localTime(zone: ZoneId = ZoneId.systemDefault()): LocalTime {
    val instant = Instant.ofEpochMilli(this)
    return instant.localTime(zone)
}

@JvmOverloads
fun Long.localDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate {
    val instant = Instant.ofEpochMilli(this)
    return instant.localDate(zone)
}

@JvmOverloads
fun Date.localTime(zone: ZoneId = ZoneId.systemDefault()): LocalTime {
    val instant = this.toInstant()
    return instant.localTime(zone)
}

@JvmOverloads
fun Date.localDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate {
    val instant = this.toInstant()
    return instant.localDate(zone)
}

fun Date.format(pattern: String): String {
    val fmt = DateTimeFormatter.ofPattern(pattern)
    return localDateTime(this).format(fmt)
}

@JvmOverloads
fun String.parseDate(pattern: String, zone: ZoneId = ZoneId.systemDefault()) = this.parseDateTime(pattern).date(zone)

fun String.parseLocalDate(pattern: String): LocalDate {
    val fmt = DateTimeFormatter.ofPattern(pattern)
    return LocalDate.parse(this, fmt)
}

fun String.parseLocalTime(pattern: String): LocalTime {
    val fmt = DateTimeFormatter.ofPattern(pattern)
    return LocalTime.parse(this, fmt)
}

fun String.parseDateTime(pattern: String): LocalDateTime {
    val fmt = DateTimeFormatter.ofPattern(pattern)
    return LocalDateTime.parse(this, fmt)
}

@JvmOverloads
fun localDateTime(date: Date, zone: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    val instant = date.toInstant()
    return instant.localDateTime(zone)
}

@JvmOverloads
fun Instant.localTime(zone: ZoneId = ZoneId.systemDefault()): LocalTime {
    val offset = zone.rules.getOffset(this)
    val localSecond = this.epochSecond + offset.totalSeconds
    val secsOfDay = Math.floorMod(localSecond, 3600.toLong() * 24).toInt()
    return LocalTime.ofNanoOfDay(secsOfDay * 1000000000L + this.nano)
}

@JvmOverloads
fun Instant.localDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate {
    val rules = zone.rules
    val offset = rules.getOffset(this)
    val localSecond = this.epochSecond + offset.totalSeconds
    val localEpochDay = Math.floorDiv(localSecond, 3600.toLong() * 24)
    return LocalDate.ofEpochDay(localEpochDay)
}

@JvmOverloads
fun Instant.localDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return LocalDateTime.ofInstant(this, zone)
}

fun localDateItr(begin: LocalDate, end: LocalDate, step: Period) = LocalDateCollection(begin, end, step)

class LocalDateCollection constructor(
    private val begin: LocalDate,
    private val end: LocalDate,
    private val step: Period
) : Iterable<LocalDate> {

    private inner class Itr(
        begin: LocalDate,
        private val end: LocalDate,
        private val step: Period
    ) : Iterator<LocalDate> {
        private var current: LocalDate = begin
        override fun hasNext(): Boolean = current.isBefore(end) || current.isEqual(end)
        @Throws(NoSuchElementException::class)
        override fun next(): LocalDate {
            val temp = current
            current += step
            return temp
        }
    }

    override fun iterator(): Iterator<LocalDate> {
        return Itr(begin, end, step)
    }
}

fun currentYear(): Int = Year.now(ZoneId.systemDefault()).value

fun getLastDateOfMonth(date: Date): Date {
    val dateTime = localDateTime(date)
    val year = dateTime.year
    val resultDateTime = dateTime.withDayOfMonth(dateTime.month.length(Year.isLeap(year.toLong())))
    return resultDateTime.date(ZoneId.systemDefault())
}