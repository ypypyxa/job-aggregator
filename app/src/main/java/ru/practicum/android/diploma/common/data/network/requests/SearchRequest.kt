package ru.practicum.android.diploma.common.data.network.requests

data class SearchRequest(
    val text: String?,
    val page: Int,
    val perPage: Int,
    val area: Int?,
    val searchField: String?,
    val industry: String?,
    val salary: Int?,
    val onlyWithSalary: Boolean
)
