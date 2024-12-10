package ru.practicum.android.diploma.root

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.R

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        // Логируем токен для проверки
        val accessToken = BuildConfig.HH_ACCESS_TOKEN
        Log.d("RootActivity", "Access Token: $accessToken")

        // Пример использования access token для HeadHunter API
        networkRequestExample(accessToken = BuildConfig.HH_ACCESS_TOKEN)
    }

    private fun networkRequestExample(accessToken: String) {
    }

}
