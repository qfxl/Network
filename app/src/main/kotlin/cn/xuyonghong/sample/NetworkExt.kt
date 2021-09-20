/*
 * Copyright (C)  XU YONGHONG, Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.xuyonghong.sample

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


fun LifecycleOwner.launch(scope: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch {
        scope()
    }
}

fun LifecycleOwner.network(scope: suspend CoroutineScope.() -> Unit): Job {
    return launch(scope)
}

fun <T> Flow<T>.request(
    onStart: suspend FlowCollector<T>.() -> Unit = {},
    onComplete: suspend FlowCollector<T>.(cause: Throwable?) -> Unit = {},
    onError: suspend FlowCollector<T>.(Throwable) -> Unit = {},
): Flow<T> {
    return onStart(onStart).catch(onError).onCompletion(onComplete)
}

suspend fun <T> Flow<T>.response(collectAction: suspend (T) -> Unit) {
    collect {
        collectAction(it)
    }
}