package ru.practicum.android.diploma.common.utils

import android.widget.TextView

fun TextView.setVacancyCountText(count: Int) {
    val UNIT = 10
    val HUNDRED = 100
    val RANGE_EXCEPTION = 12..14
    val RANGE_FEW = 2..4

    val text = when {
        count % UNIT == 1 && count % HUNDRED !in RANGE_EXCEPTION ->
            "Найдена $count вакансия"
        count % UNIT in RANGE_FEW && count % HUNDRED !in RANGE_EXCEPTION ->
            "Найдено $count вакансии"
        else ->
            "Найдено $count вакансий"
    }
    this.text = text
}
