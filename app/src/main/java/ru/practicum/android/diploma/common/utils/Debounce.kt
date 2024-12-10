package ru.practicum.android.diploma.common.utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/*
   delayMillis: Long — задержка (в миллисекундах), которая должна пройти между последним вызовом и выполнением действия.
   coroutineScope: CoroutineScope — корутинный скоуп, в котором будет выполняться задержка и действие.
   useLastParam: Boolean — если true, будет использоваться параметр из последнего вызова функции (актуально для нескольких вызовов в коротком интервале).
   actionWithDelay: Boolean — если true, действие выполняется только после истечения задержки. Если false, действие выполняется немедленно перед началом задержки.
   action: (T) -> Unit — действие, которое будет выполняться. Это лямбда-функция, принимающая параметр типа T.

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
