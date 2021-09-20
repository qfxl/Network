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

import android.os.Handler
import android.os.Looper
import android.os.Message
import cn.xuyonghong.network.config.BaseHttpConfig
import okhttp3.*
import retrofit2.Callback
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * upload manager
 */
class UploadClient private constructor() {

    private var retrofit: Retrofit? = null

    private var uploadListener: UploadListener? = null

    /**
     * dispatcher progress to main
     */
    private val dispatchHandler by lazy {
        Handler(Looper.getMainLooper()) { msg ->
            val progress = msg.arg1.toLong()
            val total = msg.arg2.toLong()
            val done = msg.obj as Boolean
            //to percent transform(100 * progress) / total
            uploadListener?.onProgress(progress, total, done)
            true
        }
    }

    /**
     * upload with progress
     *
     * @param url
     * @param uploadParams
     * @param uploadListener
     * @param callback
     */
    fun upload(
        url: String,
        uploadParams: UploadParams,
        uploadListener: UploadListener?,
        callback: Callback<ResponseBody>
    ) {
        this.uploadListener = uploadListener
        val uploadApi = retrofit?.create(UploadApi::class.java)
        val builder = MultipartBody.Builder()
        for (entry in uploadParams.entries) {
            if (entry.value is String) {
                builder.addFormDataPart(entry.key, entry.value)
            } else if (entry.value is File) {
                val file = entry.value
                builder.addFormDataPart(
                    entry.key,
                    file.name,
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
                )
            }
        }

        val body = builder.build()
        uploadApi?.upload(url, body.parts())
            ?.enqueue(callback)
    }

    companion object {
        private const val BASE_URL = "https://xuyonghong.cn/"
        @Volatile
        private var instance: UploadClient? = null

        fun get(): UploadClient = instance ?: synchronized(this) {
            instance ?: UploadClient().also {
                instance = it
            }
        }
    }

    /**
     * init
     * @param config
     */
    fun init(config: BaseHttpConfig) {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(config.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(UploadInterceptor(object : UploadListener {
                override fun onProgress(progress: Long, total: Long, done: Boolean) {
                    val msg = Message.obtain()
                    msg.arg1 = progress.toInt()
                    msg.arg2 = total.toInt()
                    msg.obj = done
                    dispatchHandler.sendMessage(msg)
                }
            }))
        retrofit = config.retrofit ?: Retrofit.Builder()
            .baseUrl(config.baseUrl ?: BASE_URL)
            .client(config.okhttpClient ?: builder.build())
            .build()
    }
}
