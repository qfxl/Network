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

package cn.xuyonghong.network.config

import cn.xuyonghong.network.BuildConfig
import cn.xuyonghong.network.header.BaseHeader
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter

class NetworkConfig : BaseHttpConfig() {
    /**
     * show log
     */
    var showLog = BuildConfig.DEBUG

    /**
     * retryOnConnectionFailure
     */
    var retryOnConnectionFailure = true

    /**
     * http header
     */
    var httpHeader: BaseHeader? = null

    /**
     * read timeout seconds
     */
    override var readTimeout = 30L

    /**
     * write timeout
     */
    override var writeTimeout = 30L

    /**
     * okhttp cache
     */
    var cache: Cache? = null

    /**
     * okHttp interceptors
     */
    var interceptorList: MutableList<Interceptor>? = null

    /**
     * retrofit convertFactories
     */
    var converterFactoryList: MutableList<Converter.Factory>? = null

    /**
     * okHttp networkInterceptors
     */
    var networkInterceptors: MutableList<Interceptor>? = null

}