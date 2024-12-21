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
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.vacancy.search.domain.model.PagedData
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearchParams
import ru.practicum.android.diploma.vacancy.search.ui.model.SearchFragmentState

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val context: Context
) : ViewModel() {

    companion object {
        const val LOADING_DELAY_MS = 2000L
        const val TAG = "SearchViewModel"
    }

    private var latestSearchText: String? = null
    private var currentPage = 0
    private var totalPages = 1
    private val pageSize = 20
    private var currentPage: Int = 0

    private val debounceSearch: (String) -> Unit = debounce(
        delayMillis = LOADING_DELAY_MS,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        searchRequest(query)
    }

    private val stateLiveData = MutableLiveData<SearchFragmentState>()
    fun observeState(): LiveData<SearchFragmentState> = mediatorStateLiveData
    private val mediatorStateLiveData = MediatorLiveData<SearchFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { searchState ->
            liveData.value = when (searchState) {
                is SearchFragmentState.Default -> searchState
                is SearchFragmentState.Content -> SearchFragmentState.Content(searchState.vacancies)
                is SearchFragmentState.Empty -> searchState
                is SearchFragmentState.ServerError -> searchState
                is SearchFragmentState.InternetError -> searchState
                is SearchFragmentState.Loading -> searchState
            }
        }
    }
    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    fun onSearchQueryChanged(query: String) {
        currentPage = 0
        if (query.isBlank()) {
            latestSearchText = null
            clearVacancies()
            return
        }

        if (latestSearchText != query) {
            latestSearchText = query
            debounceSearch(query)
        }
    }

    fun clearVacancies() {
        latestSearchText = null
        renderState(SearchFragmentState.Default)
    }

    private fun searchRequest(query: String) {
        if (query.isBlank() || latestSearchText != query) {
            return
        }

        renderState(SearchFragmentState.Loading)

        // собираем параметры запроса
        val params = VacancySearchParams(
            text = latestSearchText,
            page = 0,
            perPage = 20,
            area = 1,
            searchField = "name",
            industry = null,
            salary = null,
            onlyWithSalary = false
        )

        // выполняем запрос
        viewModelScope.launch {
            searchInteractor.fetchVacancies(params.toQueryMap())
                .collect { resource ->
                    processResult(resource.first, resource.second)
                }
        }
    }

    private fun processResult(foundVacancies: List<VacancySearch>?, errorMessage: String?) {
        val vacancies = mutableListOf<VacancySearch>()
        if (foundVacancies != null) {
            vacancies.addAll(foundVacancies)
        }
        when {
            errorMessage != null -> {
                when (errorMessage) {
                    context.getString(R.string.search_no_internet) ->
                        renderState(SearchFragmentState.InternetError)
                    else ->
                        renderState(SearchFragmentState.ServerError)
                }
                showToast.postValue(errorMessage!!)
            }
            vacancies.isEmpty() -> {
                renderState(SearchFragmentState.Empty)
            }
            else -> {
                renderState(SearchFragmentState.Content(vacancies))
            }
        }
    }

    private fun renderState(state: SearchFragmentState) {
        stateLiveData.postValue(state)
    }
}
