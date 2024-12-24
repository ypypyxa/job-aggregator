package ru.practicum.android.diploma.common.utils

import android.widget.TextView

fun TextView.setVacancyCountText(count: Int) {
    val text = when {
        count % 10 == 1 && count % 100 !in 12..14 -> "Найдена $count вакансия"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "Найдено $count вакансии"
        else -> "Найдено $count вакансий"
    }
    this.text = text
}
