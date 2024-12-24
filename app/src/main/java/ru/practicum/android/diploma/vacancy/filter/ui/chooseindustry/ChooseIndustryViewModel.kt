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
                _industryState.value = emptyList()
                Log.e(LOG_TAG, "$NETWORK_ERROR${e.message}")
            } catch (e: HttpException) {
                _industryState.value = emptyList()
                Log.e(LOG_TAG, "$SERVER_ERROR${e.code()}")
            } catch (e: IllegalStateException) {
                _industryState.value = emptyList()
                Log.e(LOG_TAG, "$ILLEGAL_STATE_ERROR${e.message}")
            }
        }
    }

    companion object {
        private const val LOG_TAG = "ChooseIndustryViewModel"
        private const val NETWORK_ERROR = "Network error: "
        private const val SERVER_ERROR = "Server error: "
        private const val ILLEGAL_STATE_ERROR = "Illegal state: "
    }

}
