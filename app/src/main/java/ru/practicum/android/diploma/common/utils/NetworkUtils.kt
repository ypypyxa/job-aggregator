package ru.practicum.android.diploma.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    /**
    Context
    Метод требует Context для получения ConnectivityManager, который управляет сетевыми подключениями устройства.

    ConnectivityManager
    Используемый системный сервис для управления сетями. Мы получаем активную сеть через connectivityManager.activeNetwork.

    NetworkCapabilities
    Проверяем, может ли текущая сеть обеспечивать доступ к интернету. Это делается через вызов hasCapability с параметром NET_CAPABILITY_INTERNET.

    Обработка null значений

    Если активная сеть (activeNetwork) отсутствует, возвращаем false.
    Если NetworkCapabilities отсутствуют, также возвращаем false.
     */
    fun isInternetAvailable(context: Context): Boolean {
        // Получаем системную службу для управления сетевыми подключениями
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Проверяем текущее активное сетевое подключение
        val activeNetwork = connectivityManager.activeNetwork ?: return false

        // Получаем способности (capabilities) текущей сети
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        // Проверяем наличие возможности подключения к интернету
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
