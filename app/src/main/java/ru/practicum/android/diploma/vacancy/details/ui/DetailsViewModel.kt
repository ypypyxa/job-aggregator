package ru.practicum.android.diploma.vacancy.details.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsInteractor
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails

class DetailsViewModel(
    private val detailsInteractor: DetailsInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _vacancyDetails = MutableLiveData<VacancyDetails?>()
    val vacancyDetails: LiveData<VacancyDetails?> get() = _vacancyDetails

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
                Log.d("ErrorMessage", errorMessage)
            }
            vacancyDetails == null -> {
                Log.d("ErrorMesagge", "Вакансий не найдено")
            }
            else -> {
                Log.d("VacancyResult", vacancy.toString())
                _vacancyDetails.value = vacancyDetails
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
}
