package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import java.io.IOException

class ChooseIndustryViewModel(
    private val interactor: IndustryFilterInteractor
) : ViewModel() {

    private val _industryState = MutableStateFlow<List<FilterIndustryValue>>(emptyList())
    val industryState: StateFlow<List<FilterIndustryValue>> = _industryState

    init {
        fetchIndustries()
    }

    private fun fetchIndustries() {
        viewModelScope.launch {
            try {
                val industries = interactor.fetchIndustries()
                _industryState.value = industries
            } catch (e: IOException) {
                // Сетевая ошибка (отключен интернет)
                _industryState.value = emptyList()
                Log.e("ChooseIndustryViewModel", "Network error: ${e.message}")
            } catch (e: HttpException) {
                // Сервер вернул ошибку
                _industryState.value = emptyList()
                Log.e("ChooseIndustryViewModel", "Server error: ${e.code()}")
            } catch (e: Exception) {
                // Любая другая ошибка
                _industryState.value = emptyList()
                Log.e("ChooseIndustryViewModel", "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

}
