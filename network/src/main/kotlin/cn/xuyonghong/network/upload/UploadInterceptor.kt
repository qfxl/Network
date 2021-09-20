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

package cn.xuyonghong.network.upload

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * upload interceptor
 */
class UploadInterceptor(private val uploadListener: UploadListener) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        if (null == originRequest.body()) {
            return chain.proceed(originRequest)
        }
        val newRequest = originRequest.newBuilder()
                .method(originRequest.method(), ProgressRequestBody(originRequest.body()!!, uploadListener))
                .build()
        return chain.proceed(newRequest)
    }
}
