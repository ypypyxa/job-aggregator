package ru.practicum.android.diploma.common.utils

sealed class Resource<T>(
    val data: T? = null,
    val errorCode: Int? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(errorCode: Int, message: String? = null) : Resource<T>(errorCode = errorCode, message = message)

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> error(errorCode: Int, message: String? = null): Resource<T> = Error(errorCode, message)
    }
}
