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

package cn.xuyonghong.network

import cn.xuyonghong.network.config.NetworkConfig
import cn.xuyonghong.network.interceptor.HeaderInterceptor
import cn.xuyonghong.network.logger.HttpLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class NetworkClient {
    companion object {
        @Volatile
        private var instance: NetworkClient? = null

        fun get(): NetworkClient = instance ?: synchronized(this) {
            instance ?: NetworkClient().also {
                instance = it
            }
        }
    }

    private lateinit var mRetrofit: Retrofit

    fun init(config: NetworkConfig) {
        val mOkHttpClient = config.okhttpClient ?: OkHttpClient.Builder().apply {
            connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
            readTimeout(config.readTimeout, TimeUnit.SECONDS)
            retryOnConnectionFailure(config.retryOnConnectionFailure)
            config.cache?.let { c ->
                cache(c)
            }
            config.httpHeader?.let { header ->
                addInterceptor(HeaderInterceptor(header))
            }
            if (config.showLog) {
                val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLogger(config.logTag))
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(httpLoggingInterceptor)
            }
            config.interceptorList?.forEach {
                addInterceptor(it)
            }
            config.networkInterceptors?.forEach {
                addNetworkInterceptor(it)
            }
        }.build()
        //init retrofit
        mRetrofit = Retrofit.Builder().apply {
            client(mOkHttpClient)
            baseUrl(config.baseUrl)
            addConverterFactory(GsonConverterFactory.create())
            config.converterFactoryList?.forEach {
                addConverterFactory(it)
            }
        }.build()
    }

    /**
     * create
     * @param service
     */
    fun <T> create(service: Class<T>) = mRetrofit.create(service)
}