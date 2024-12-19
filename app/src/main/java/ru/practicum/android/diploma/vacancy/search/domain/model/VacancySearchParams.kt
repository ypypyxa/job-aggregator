package ru.practicum.android.diploma.vacancy.search.domain.model

/**
 * Класс, представляющий параметры для поиска вакансий.
 *
 * @property text Текстовый запрос для поиска вакансий.
 * @property page Номер страницы результатов поиска (по умолчанию 0).
 * @property perPage Количество вакансий на странице (по умолчанию 20).
 * @property area Идентификатор региона для фильтрации вакансий.
 * @property searchField Поле для поиска (например, "name").
 * @property industry Идентификатор отрасли для фильтрации вакансий.
 * @property salary Минимальная заработная плата для фильтрации вакансий.
 * @property onlyWithSalary Фильтр для отображения только вакансий с указанной зарплатой (по умолчанию false).
 */
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
    /**
     * Преобразует параметры поиска в карту (Map), пригодную для использования в запросах API.
     *
     * @return Карта с параметрами запроса.
     */
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
