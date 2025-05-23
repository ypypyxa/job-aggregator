package ru.practicum.android.diploma.vacancy.search.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.SingleLiveEvent
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.vacancy.filter.domain.FilterSettingsInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.vacancy.search.domain.model.PagedData
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearchParams
import ru.practicum.android.diploma.vacancy.search.ui.model.SearchFragmentState

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val filterSettingsInteractor: FilterSettingsInteractor,
    private val context: Context
) : ViewModel() {

    private var previousFilterSettings: FilterSettings? = null

    private var currentFilterSettings: FilterSettings? = null

    companion object {
        const val LOADING_DELAY_MS = 2000L
        const val TAG = "SearchViewModel"
        const val PAGE_SIZE = 20
    }

    private var onlyWithSalary: Boolean = false

    var latestSearchText: String? = null
    var currentPage: Int = 0
    private var totalPages = 1
    private val pageSize = PAGE_SIZE
    private var totalVacancies = 0

    private val debounceSearch: (String) -> Unit = debounce(
        delayMillis = LOADING_DELAY_MS,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        if (latestSearchText != query) {
            searchRequest(query)
        }
    }

    private val stateLiveData = MutableLiveData<SearchFragmentState>()
    fun observeState(): LiveData<SearchFragmentState> = mediatorStateLiveData
    private val mediatorStateLiveData = MediatorLiveData<SearchFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { searchState ->
            liveData.value = when (searchState) {
                is SearchFragmentState.Default -> searchState
                is SearchFragmentState.Content -> SearchFragmentState.Content(
                    searchState.vacancies,
                    searchState.vacanciesCount
                )

                is SearchFragmentState.Empty -> searchState
                is SearchFragmentState.ServerError -> searchState
                is SearchFragmentState.InternetError -> searchState
                is SearchFragmentState.Loading -> searchState
                is SearchFragmentState.UpdateList -> searchState
            }
        }
    }
    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    fun onSearchQueryChanged(query: String, forceUpdate: Boolean = false) {
        val isFilterChanged = currentFilterSettings != previousFilterSettings
        if (forceUpdate || isFilterChanged || query != latestSearchText) {
            currentPage = 0
            if (query.isBlank()) {
                latestSearchText = null
                clearVacancies()
                return
            }
            if (forceUpdate || isFilterChanged) {
                latestSearchText = query
                searchRequest(query)
            } else {
                debounceSearch(query)
            }
        }
    }

    fun onSearchButtonPress(query: String) {
        if (latestSearchText == null) {
            searchRequest(query)
        }
    }

    fun clearVacancies() {
        latestSearchText = null
        renderState(SearchFragmentState.Default)
    }

    private fun searchRequest(query: String) {
        latestSearchText = query

        renderState(SearchFragmentState.Loading)

        // собираем параметры запроса
        val params = VacancySearchParams(
            text = latestSearchText,
            page = currentPage,
            perPage = pageSize,
            area = currentFilterSettings?.region?.id?.toIntOrNull(),
            searchField = "name",
            industry = currentFilterSettings?.industry?.id,
            salary = currentFilterSettings?.expectedSalary.takeIf { it!! >= 0 },
            onlyWithSalary = currentFilterSettings?.notShowWithoutSalary ?: onlyWithSalary
        )

        // выполняем запрос
        viewModelScope.launch {
            searchInteractor.fetchVacancies(params.toQueryMap())
                .collect { resource ->
                    processResult(resource.first, resource.second)
                }
        }
    }

    private fun processResult(result: PagedData<VacancySearch>?, errorMessage: String?) {
        val newVacancies = result?.items ?: emptyList()
        when {
            errorMessage != null -> {
                when (errorMessage) {
                    context.getString(R.string.search_no_internet) ->
                        renderState(SearchFragmentState.InternetError)

                    else ->
                        renderState(SearchFragmentState.ServerError)
                }
                latestSearchText = null
                showToast.postValue(errorMessage!!)
                _isLoading = false
            }

            newVacancies.isEmpty() && currentPage == 0 -> {
                renderState(SearchFragmentState.Empty)
                _isLoading = false
            }

            else -> {
                val currentState = stateLiveData.value
                val updatedVacancies = when {
                    currentPage == 0 -> newVacancies
                    currentState is SearchFragmentState.Content -> currentState.vacancies + newVacancies
                    else -> newVacancies
                }
                result?.let {
                    totalPages = it.totalPages
                    totalVacancies = it.totalItems
                }

                renderState(SearchFragmentState.Content(updatedVacancies, totalVacancies))
                _isLoading = false
            }
        }
    }

    private fun renderState(state: SearchFragmentState) {
        stateLiveData.postValue(state)
    }

    fun loadNextPage() {
        if (currentPage >= totalPages || _isLoading) {
            return
        }

        _isLoading = true
        currentPage++
        renderState(SearchFragmentState.UpdateList)

        val params = VacancySearchParams(
            text = latestSearchText,
            page = currentPage,
            perPage = pageSize,
            area = currentFilterSettings?.region?.id?.toIntOrNull(),
            searchField = "name",
            industry = currentFilterSettings?.industry?.id,
            salary = currentFilterSettings?.expectedSalary.takeIf { it!! >= 0 },
            onlyWithSalary = currentFilterSettings?.notShowWithoutSalary ?: onlyWithSalary
        )

        viewModelScope.launch {
            searchInteractor.fetchVacancies(params.toQueryMap())
                .collect { resource ->
                    processResult(resource.first, resource.second)
                }
        }
    }

    fun setOnlyWithSalary(value: Boolean) {
        onlyWithSalary = value
    }

    private var _isLoading = false
    val isLoading: Boolean get() = _isLoading

    fun getFilterSettings(callback: (FilterSettings?) -> Unit) {
        viewModelScope.launch {
            val settings = filterSettingsInteractor.getFilterSettings()
            val isFilterChanged = settings != previousFilterSettings
            previousFilterSettings = settings
            currentFilterSettings = settings
            callback(settings)

            if (isFilterChanged) {
                onSearchQueryChanged(latestSearchText ?: "", forceUpdate = true)
            }
        }
    }

}
