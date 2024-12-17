package ru.practicum.android.diploma.common.data.network

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.common.data.dto.Response
import ru.practicum.android.diploma.common.data.network.requests.AreaRequest
import ru.practicum.android.diploma.common.data.network.requests.IndustryRequest
import ru.practicum.android.diploma.common.data.network.requests.VacancyDetailRequest
import ru.practicum.android.diploma.common.data.network.requests.SearchRequest
import ru.practicum.android.diploma.common.data.network.response.AreaResponse
import ru.practicum.android.diploma.common.data.network.response.IndustryResponse
import ru.practicum.android.diploma.common.data.network.response.VacancyDetailResponse
import ru.practicum.android.diploma.common.utils.isInternetAvailable
import java.io.IOException

/**
 * Вызов метода doRequest:
 * Проверяет наличие интернета.
 * Вызывает doRequestInternal(dto).
 *
 * doRequestInternal:
 *
 * Определяет тип DTO и вызывает нужный метод API.
 *
 * Обработка ошибок:
 *
 * Если запрос не удался, возвращает соответствующий результат (NO_INTERNET_ERROR, CLIENT_ERROR, SERVER_ERROR).
 *
 * Возвращает Response:
 *
 * Сетевой запрос завершился успешно — возвращается результат с resultCode = SUCCESS.
 */

class RetrofitNetworkClient(
    private val headHunterApi: HeadHunterApi,
    private val context: Context
) : NetworkClient {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun doRequest(dto: Any): Response {
        if (!context.isInternetAvailable()) return Response().apply { resultCode = NO_INTERNET_ERROR }
        return withContext(Dispatchers.IO) {
            try {
                doRequestInternal(dto)
            } catch (e: IOException) {
                e.printStackTrace()
                Response().apply { resultCode = NO_INTERNET_ERROR }
            } catch (e: HttpException) {
                e.printStackTrace()
                getHttpExceptionResponse()
            } catch (e: RuntimeException) {
                e.printStackTrace()
                getRuntimeExceptionResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                Response().apply { resultCode = SERVER_ERROR }
            }
        }
    }

    //поиск вакансий
    private suspend fun makeVacancySearchRequest(dto: SearchRequest): Response {
        return headHunterApi.getVacancies(
            text = dto.text,
            page = dto.page,
            perPage = dto.perPage,
            area = dto.area,
            industry = dto.industry,
            salary = dto.salary,
            onlyWithSalary = dto.onlyWithSalary
        ).apply {
            resultCode = SUCCESS
        }
    }

    //получение конкретной вакансии:
    private suspend fun makeSingleVacancyRequest(dto: VacancyDetailRequest): Response {
        return VacancyDetailResponse(vacancy = headHunterApi.getVacancy(vacancyId = dto.vacancyId)).apply {
            resultCode = SUCCESS
        }
    }


    //регионы
    private suspend fun makeAreasRequest(dto: AreaRequest): Response {
        dto.id?.let {
            return AreaResponse(areas = listOf(headHunterApi.getAreaById(areaId = it))).apply {
                resultCode = SUCCESS
            }
        }
        return AreaResponse(areas = headHunterApi.getAreas()).apply {
            resultCode = SUCCESS
        }
    }

    //отрасль
    private suspend fun makeIndustriesRequest(dto: IndustryRequest): Response {
        return IndustryResponse(industries = headHunterApi.getIndustries()).apply {
            resultCode = SUCCESS
        }
    }

    //распределяющий метод, который вызывает конкретные методы на основе типа DTO
    private suspend fun doRequestInternal(dto: Any): Response {
        return when (dto) {
            is SearchRequest -> return makeVacancySearchRequest(dto)

            is AreaRequest -> makeAreasRequest(dto)

            is VacancyDetailRequest -> makeSingleVacancyRequest(dto)

            is IndustryRequest -> makeIndustriesRequest(dto)

            else -> Response().apply { resultCode = CLIENT_ERROR }
        }
    }

    //Обработка HttpException
    private fun getHttpExceptionResponse(): Response {
        return Response().apply { resultCode = CLIENT_ERROR }
    }

    //Обработка RuntimeException
    private fun getRuntimeExceptionResponse(): Response {
        return Response().apply { resultCode = CLIENT_ERROR }
    }

    companion object {
        private const val CLIENT_ERROR = 400
        private const val SERVER_ERROR = 500
        private const val NO_INTERNET_ERROR = -1
        private const val SUCCESS = 200
    }
}
