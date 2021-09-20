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

package cn.xuyonghong.network.download

import android.os.Handler
import android.os.Looper
import android.os.Message
import cn.xuyonghong.network.config.BaseHttpConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * download manager
 */
class DownloadClient {
    companion object {
        private const val BASE_URL = "https://xuyonghong.cn/"
        @Volatile
        private var instance: DownloadClient? = null

        fun get(): DownloadClient = instance ?: synchronized(this) {
            instance ?: DownloadClient().also {
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
            .networkInterceptors()
            .add(Interceptor { chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder().body(
                    ProgressResponseBody(originalResponse.body()!!, object :
                        ProgressListener {
                        override fun onProgress(progress: Long, total: Long, done: Boolean) {
                            val msg = Message.obtain()
                            msg.arg1 = progress.toInt()
                            msg.arg2 = total.toInt()
                            msg.obj = done
                            dispatchHandler.sendMessage(msg)
                        }

                    })
                ).build()
            })
        retrofit = config.retrofit ?: Retrofit.Builder()
            .baseUrl(config.baseUrl ?: BASE_URL)
            .client(config.okhttpClient ?: builder.build())
            .build()
    }

    /**
     * core
     */
    private var retrofit: Retrofit? = null

    /**
     * progress
     */
    private var mProgressListener: ProgressListener? = null

    /**
     * post progress to main
     */
    private val dispatchHandler by lazy {
        Handler(Looper.getMainLooper()) { msg ->
            val progress = msg.arg1.toLong()
            val total = msg.arg2.toLong()
            val done = msg.obj as Boolean
            //if show percent (100 * progress) / total
            mProgressListener?.onProgress(progress, total, done)
            true
        }
    }


    /**
     * download with progress
     *
     * @param url              下载的url
     * @param destFile         存储目标文件
     * @param progressListener 进度监听
     */
    fun download(url: String, destFile: File?, progressListener: ProgressListener?, downloadListener: OnDownloadListener?) {
        mProgressListener = progressListener
        val downloadApi = retrofit?.create(DownloadApi::class.java)
        downloadApi?.download(url)
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: retrofit2.Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    saveFileToLocal(destFile, response.body())
                    downloadListener?.onDownloadSuccess(destFile)
                }

                override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                    downloadListener?.onDownloadFailed(t.message)
                }
            })
    }

    /**
     * save file to local
     *
     * @param destFile
     * @param responseBody
     */
    private fun saveFileToLocal(destFile: File?, responseBody: ResponseBody?) {
        if (responseBody != null && destFile != null) {
            val ins = responseBody.byteStream()
            destFile.writeBytes(ins.readBytes())
            ins.close()
        }
    }

    interface OnDownloadListener {
        /**
         * download success
         * @param file
         */
        fun onDownloadSuccess(file: File?)

        /**
         * download failed
         * @param errorMsg
         */
        fun onDownloadFailed(errorMsg: String?)
    }
}
