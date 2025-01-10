package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.common.utils.isInternetAvailable
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import java.io.IOException

class ChooseIndustryViewModel(
    private val interactor: IndustryFilterInteractor,
    private val context: Context
    ) : ViewModel() {

    private val _industryState = MutableStateFlow<List<FilterIndustryValue>>(emptyList())
    val industryState: StateFlow<List<FilterIndustryValue>> = _industryState

    private val _selectedIndustry = MutableStateFlow<FilterIndustryValue?>(null)
    val selectedIndustry: StateFlow<FilterIndustryValue?> = _selectedIndustry

    private var allIndustries: List<FilterIndustryValue> = emptyList()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError

    init {
        fetchIndustries()
    }

    fun fetchIndustries() {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            try {
                if (!context.isInternetAvailable()) {
                    _hasError.value = true
                    _industryState.value = emptyList()
                    return@launch
                }
                val industries = interactor.fetchIndustries()
                allIndustries = industries
                _industryState.value = industries
                _hasError.value = industries.isEmpty()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "$NETWORK_ERROR${e.message}")
                _industryState.value = emptyList()
                _hasError.value = true
            } catch (e: HttpException) {
                Log.e(LOG_TAG, "$SERVER_ERROR${e.code()}")
                _industryState.value = emptyList()
                _hasError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun filterIndustries(query: String) {
        val filteredList = if (query.isEmpty()) {
            allIndustries
        } else {
            allIndustries.filter {
                it.text?.contains(query, ignoreCase = true) == true
            }
        }
        _industryState.value = filteredList
        _hasError.value = filteredList.isEmpty()
    }

    fun selectIndustry(industry: FilterIndustryValue?) {
        _selectedIndustry.value = industry
    }

    companion object {
        private const val LOG_TAG = "ChooseIndustryViewModel"
        private const val NETWORK_ERROR = "Network error: "
        private const val SERVER_ERROR = "Server error: "
    }

}
