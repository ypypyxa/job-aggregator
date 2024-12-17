package ru.practicum.android.diploma.common.data.network.requests

data class SearchRequest(
    val text: String? = null,
    val page: Int = 0,
    val perPage: Int = 20,
    val searchField: String? = "name",
    val area: Int? = null,
    val industry: String? = null,
    val salary: Int? = null,
    val onlyWithSalary: Boolean = false
)
