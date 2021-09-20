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

import android.app.Application
import cn.xuyonghong.network.NetworkClient
import cn.xuyonghong.network.config.BaseHttpConfig
import cn.xuyonghong.network.config.NetworkConfig
import cn.xuyonghong.network.download.DownloadClient
import cn.xuyonghong.network.upload.UploadClient

/**
 * author : qfxl
 * e-mail : xuyonghong0822@gmail.com
 * time   : 2021/09/20
 * desc   :
 * version: 1.0
 */

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        NetworkClient.get().init(NetworkConfig().apply {
            baseUrl = "https://www.wanandroid.com"
        })
        DownloadClient.get().init(BaseHttpConfig())
        UploadClient.get().init(BaseHttpConfig())
    }
}