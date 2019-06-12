package com.hellmund.droidcon2019.data.model

import org.threeten.bp.LocalDate

enum class EventDay {
    Monday, Tuesday, Wednesday;

    fun toDate(): LocalDate {
        return when (this) {
            Monday -> LocalDate.of(2019, 7, 1)
            Tuesday -> LocalDate.of(2019, 7, 2)
            Wednesday -> LocalDate.of(2019, 7, 3)
        }
    }
}