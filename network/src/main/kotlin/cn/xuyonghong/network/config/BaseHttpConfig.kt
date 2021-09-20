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

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * author : qfxl
 * e-mail : xuyonghong0822@gmail.com
 * time   : 2021/09/20
 * desc   :
 * version: 1.0
 */

open class BaseHttpConfig {
    /**
     * base url
     */
    var baseUrl: String? = null

    /**
     * okhttp log filter
     */
    var logTag = "okhttp"

    /**
     * connect timeout
     */
    open var connectTimeout = 30L

    /**
     * read timeout seconds
     */
    open var readTimeout = 5 * 60L

    /**
     * write timeout
     */
    open var writeTimeout = 5 * 60L

    /**
     * if not null, retrofit will use this okhttp client
     */
    open var okhttpClient: OkHttpClient? = null

    /**
     * if not null, http core will use this
     */
    var retrofit: Retrofit? = null
}