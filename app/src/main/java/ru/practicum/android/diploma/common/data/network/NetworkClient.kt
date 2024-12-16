package ru.practicum.android.diploma.common.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import ru.practicum.android.diploma.BuildConfig

object NetworkClient {

    private const val BASE_URL = "https://api.hh.ru/"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.HH_ACCESS_TOKEN}")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()


}
