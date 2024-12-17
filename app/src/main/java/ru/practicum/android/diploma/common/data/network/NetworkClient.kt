package ru.practicum.android.diploma.common.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.common.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}
