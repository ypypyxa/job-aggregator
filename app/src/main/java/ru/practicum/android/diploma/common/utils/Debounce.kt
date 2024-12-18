package ru.practicum.android.diploma.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Создает функцию с механизмом дебаунса.
 *
 * @param delayMillis Задержка (в миллисекундах), которая должна пройти между последним вызовом и выполнением действия.
 * @param coroutineScope Корутинный скоуп, в котором будет выполняться задержка и действие.
 * @param useLastParam Если true, будет использоваться параметр из последнего вызова функции.
 * @param actionWithDelay Если true, действие выполняется только после истечения задержки.
 * Если  false, действие выполняется немедленно перед началом задержки.
 * @param action Лямбда-функция, которая выполняет нужное действие с параметром типа T.
 * @return Функция с дебаунсом, принимающая параметр типа T.
 */
fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParam: Boolean,
    actionWithDelay: Boolean = true,
    action: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            if (!actionWithDelay) action(param)
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                if (actionWithDelay) action(param)
            }
        }
    }
}
