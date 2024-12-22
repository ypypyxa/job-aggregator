package ru.practicum.android.diploma.vacancy.details.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.SingleLiveEvent
import ru.practicum.android.diploma.common.utils.isInternetAvailable
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsInteractor
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.details.ui.model.DetailsFragmentState

class DetailsViewModel(
    private val detailsInteractor: DetailsInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val context: Context,
    private val vacancyId: Int
) : ViewModel() {

    private val stateLiveData = MutableLiveData<DetailsFragmentState>()
    fun observeState(): LiveData<DetailsFragmentState> = mediatorStateLiveData
    private val mediatorStateLiveData = MediatorLiveData<DetailsFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { detailsState ->
            liveData.value = when (detailsState) {
                is DetailsFragmentState.Loading -> detailsState
                is DetailsFragmentState.ServerError -> detailsState
                is DetailsFragmentState.Content -> DetailsFragmentState.Content(detailsState.vacancy)
                is DetailsFragmentState.OfflineContent -> DetailsFragmentState.OfflineContent(detailsState.vacancy)
                else -> DetailsFragmentState.Empty
            }
        }
    }

    init {
        stateLiveData.postValue(DetailsFragmentState.Loading)

        if (context.isInternetAvailable()) {
            loadVacancy(vacancyId)
        } else {
            loadVacancyDetailsOffline(vacancyId)
        }
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private val _isFavorite = MutableStateFlow<Boolean?>(null)
    val isFavorite: StateFlow<Boolean?> = _isFavorite

    fun loadVacancy(vacancyId: Int) {
        viewModelScope.launch {
            detailsInteractor.fetchDetails(vacancyId)
                .collect { resource ->
                    processResult(resource.first, resource.second)
                    resource.first?.let {
                        checkIfFavorite(it.vacancyId)
                    }
                }

        }
    }

    private fun processResult(vacancyDetails: VacancyDetails?, errorMessage: String?) {
        var vacancy = vacancyDetails
        when {
            errorMessage != null -> {
                when (errorMessage) {
                    context.getString(R.string.vacancy_not_found) -> renderState(DetailsFragmentState.Empty)
                    context.getString(R.string.server_error) -> renderState(DetailsFragmentState.ServerError)
                }
            }
            vacancyDetails == null -> {
                renderState(DetailsFragmentState.Empty)
            }
            else -> {
                renderState(DetailsFragmentState.Content(vacancyDetails))
            }
        }
    }
    fun addToFavorites(vacancy: VacancyDetails) {
        viewModelScope.launch {
            favoritesInteractor.addFavoriteVacancy(vacancy)
            _isFavorite.value = true
        }
    }
    fun removeFromFavorites(vacancyId: Int) {
        viewModelScope.launch {
            favoritesInteractor.removeFavoriteVacancy(vacancyId)
            _isFavorite.value = false
        }
    }

    private fun checkIfFavorite(vacancyId: Int) {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isVacancyFavorite(vacancyId)
            _isFavorite.value = isFavorite
        }
    }
    fun loadVacancyDetailsOffline(vacancyId: Int) {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancy(vacancyId).collect { details ->
                renderState(DetailsFragmentState.OfflineContent(details))
                _isFavorite.value = details != null
            }
        }
    }

    private fun renderState(state: DetailsFragmentState) {
        stateLiveData.postValue(state)
    }
}
