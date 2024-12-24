package ru.practicum.android.diploma.common.utils

import android.widget.TextView

fun TextView.setVacancyCountText(count: Int) {
    val unit = 10
    val hundred = 100
    val rangeException = 12..14
    val rangeFew = 2..4

    val text = when {
        count % unit == 1 && count % hundred !in rangeException ->
            "Найдена $count вакансия"
        count % unit in rangeFew && count % hundred !in rangeException ->
            "Найдено $count вакансии"
        else ->
            "Найдено $count вакансий"
    }
    this.text = text
}
