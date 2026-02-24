package com.example.ramazontaqvim.widget


import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// ── Data ────────────────────────────────────────────────────────────
data class RamazonKun(
    val kun: Int,
    val sana: LocalDate,
    val sahar: LocalTime,
    val iftor: LocalTime
)

@RequiresApi(Build.VERSION_CODES.O)
val RAMAZON_2026 = listOf(
    RamazonKun(1,  LocalDate.of(2026,2,19),  LocalTime.of(5,54), LocalTime.of(18,5)),
    RamazonKun(2,  LocalDate.of(2026,2,20),  LocalTime.of(5,53), LocalTime.of(18,7)),
    RamazonKun(3,  LocalDate.of(2026,2,21),  LocalTime.of(5,51), LocalTime.of(18,8)),
    RamazonKun(4,  LocalDate.of(2026,2,22),  LocalTime.of(5,50), LocalTime.of(18,9)),
    RamazonKun(5,  LocalDate.of(2026,2,23),  LocalTime.of(5,49), LocalTime.of(18,10)),
    RamazonKun(6,  LocalDate.of(2026,2,24),  LocalTime.of(5,47), LocalTime.of(18,11)),
    RamazonKun(7,  LocalDate.of(2026,2,25),  LocalTime.of(5,46), LocalTime.of(18,13)),
    RamazonKun(8,  LocalDate.of(2026,2,26),  LocalTime.of(5,44), LocalTime.of(18,14)),
    RamazonKun(9,  LocalDate.of(2026,2,27),  LocalTime.of(5,43), LocalTime.of(18,15)),
    RamazonKun(10, LocalDate.of(2026,2,28),  LocalTime.of(5,41), LocalTime.of(18,16)),
    RamazonKun(11, LocalDate.of(2026,3,1),   LocalTime.of(5,40), LocalTime.of(18,17)),
    RamazonKun(12, LocalDate.of(2026,3,2),   LocalTime.of(5,38), LocalTime.of(18,19)),
    RamazonKun(13, LocalDate.of(2026,3,3),   LocalTime.of(5,37), LocalTime.of(18,20)),
    RamazonKun(14, LocalDate.of(2026,3,4),   LocalTime.of(5,35), LocalTime.of(18,21)),
    RamazonKun(15, LocalDate.of(2026,3,5),   LocalTime.of(5,34), LocalTime.of(18,22)),
    RamazonKun(16, LocalDate.of(2026,3,6),   LocalTime.of(5,32), LocalTime.of(18,23)),
    RamazonKun(17, LocalDate.of(2026,3,7),   LocalTime.of(5,31), LocalTime.of(18,24)),
    RamazonKun(18, LocalDate.of(2026,3,8),   LocalTime.of(5,29), LocalTime.of(18,25)),
    RamazonKun(19, LocalDate.of(2026,3,9),   LocalTime.of(5,27), LocalTime.of(18,27)),
    RamazonKun(20, LocalDate.of(2026,3,10),  LocalTime.of(5,26), LocalTime.of(18,28)),
    RamazonKun(21, LocalDate.of(2026,3,11),  LocalTime.of(5,24), LocalTime.of(18,29)),
    RamazonKun(22, LocalDate.of(2026,3,12),  LocalTime.of(5,22), LocalTime.of(18,30)),
    RamazonKun(23, LocalDate.of(2026,3,13),  LocalTime.of(5,21), LocalTime.of(18,31)),
    RamazonKun(24, LocalDate.of(2026,3,14),  LocalTime.of(5,19), LocalTime.of(18,32)),
    RamazonKun(25, LocalDate.of(2026,3,15),  LocalTime.of(5,17), LocalTime.of(18,33)),
    RamazonKun(26, LocalDate.of(2026,3,16),  LocalTime.of(5,15), LocalTime.of(18,34)),
    RamazonKun(27, LocalDate.of(2026,3,17),  LocalTime.of(5,14), LocalTime.of(18,35)),
    RamazonKun(28, LocalDate.of(2026,3,18),  LocalTime.of(5,12), LocalTime.of(18,37)),
    RamazonKun(29, LocalDate.of(2026,3,19),  LocalTime.of(5,10), LocalTime.of(18,38)),
    RamazonKun(30, LocalDate.of(2026,3,20),  LocalTime.of(5,8),  LocalTime.of(18,39)),
)

// ── Widget State ─────────────────────────────────────────────────────
enum class RamazonPhase {
    SAHAR_WAIT,   // sahargacha
    SAHAR_DONE,   // sahar bo‘ldi, ro'za boshlandi
    ROZA,         // iftorgacha
    IFTOR_DONE    // iftor bo‘ldi, keyingi sahargacha
}
data class WidgetState(
    val kun: Int,
    val saharTime: String,
    val iftorTime: String,
    val phase: RamazonPhase,
    val countdownText: String,
    val progressPercent: Int,
    val showDuo: Boolean,
    val duoForIftor: Boolean,
    val isRamazon: Boolean
)

// ── State Calculator ─────────────────────────────────────────────────
@RequiresApi(Build.VERSION_CODES.O)
fun calculateWidgetState(now: LocalDateTime = LocalDateTime.now()): WidgetState {

    val today = now.toLocalDate()

    val todayData = RAMAZON_2026.find { it.sana == today }
    val tomorrowData = RAMAZON_2026.find { it.sana == today.plusDays(1) }

    val isRamazon = todayData != null

    val data = todayData ?: RAMAZON_2026.first()

    val saharDt = LocalDateTime.of(data.sana, data.sahar)
    val iftorDt = LocalDateTime.of(data.sana, data.iftor)

    val phase: RamazonPhase
    val targetTime: LocalDateTime

    when {
        now.isBefore(saharDt) -> {
            phase = RamazonPhase.SAHAR_WAIT
            targetTime = saharDt
        }

        now.isAfter(saharDt) && now.isBefore(iftorDt) -> {
            phase = RamazonPhase.ROZA
            targetTime = iftorDt
        }

        else -> {
            phase = RamazonPhase.IFTOR_DONE

            val nextSahar = tomorrowData?.let {
                LocalDateTime.of(it.sana, it.sahar)
            } ?: saharDt.plusDays(1)

            targetTime = nextSahar
        }
    }

    val countdownMs = Duration.between(now, targetTime).toMillis()

    val progress = when (phase) {

        RamazonPhase.SAHAR_WAIT -> {
            val total = Duration.between(
                LocalDateTime.of(data.sana, LocalTime.MIDNIGHT),
                saharDt
            ).toMillis()

            val elapsed = Duration.between(
                LocalDateTime.of(data.sana, LocalTime.MIDNIGHT),
                now
            ).toMillis()

            ((elapsed.toFloat() / total) * 100).toInt().coerceIn(0, 100)
        }

        RamazonPhase.ROZA -> {
            val total = Duration.between(saharDt, iftorDt).toMillis()
            val elapsed = Duration.between(saharDt, now).toMillis()

            ((elapsed.toFloat() / total) * 100).toInt().coerceIn(0, 100)
        }

        RamazonPhase.IFTOR_DONE -> {
            val total = Duration.between(iftorDt, targetTime).toMillis()
            val elapsed = Duration.between(iftorDt, now).toMillis()

            ((elapsed.toFloat() / total) * 100).toInt().coerceIn(0, 100)
        }

        RamazonPhase.SAHAR_DONE -> 100
    }

    fun fmt(ms: Long): String {
        val s = ms / 1000
        return "%02d:%02d".format(s / 3600, (s % 3600) / 60)
    }

    fun fmtTime(t: LocalTime) =
        "%02d:%02d".format(t.hour, t.minute)

    return WidgetState(
        kun = data.kun,
        saharTime = fmtTime(data.sahar),
        iftorTime = fmtTime(data.iftor),
        phase = phase,
        countdownText = fmt(countdownMs),
        progressPercent = progress,
        showDuo = countdownMs <= 3_600_000,
        duoForIftor = phase == RamazonPhase.ROZA,
        isRamazon = isRamazon
    )
}
