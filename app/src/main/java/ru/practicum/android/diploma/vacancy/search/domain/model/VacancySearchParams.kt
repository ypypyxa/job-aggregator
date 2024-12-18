package ru.practicum.android.diploma.vacancy.search.domain.model

class VacancySearchParams(
    val text: String? = null,
    val page: Int = 0,
    val perPage: Int = 20,
    val area: Int? = null,
    val searchField: String? = null,
    val industry: String? = null,
    val salary: Int? = null,
    val onlyWithSalary: Boolean = false
) {
    fun toQueryMap(): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            text?.let { put("text", it) }
            put("page", page)
            put("per_page", perPage)
            area?.let { put("area", it) }
            searchField?.let { put("search_field", it) }
            industry?.let { put("industry", it) }
            salary?.let { put("salary", it) }
            put("only_with_salary", onlyWithSalary)
        }
    }
}
