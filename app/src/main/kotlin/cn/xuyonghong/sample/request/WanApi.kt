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

package cn.xuyonghong.sample.request

import cn.xuyonghong.network.NetworkClient
import cn.xuyonghong.sample.response.BannerResponse
import cn.xuyonghong.sample.response.BaseResponse
import com.google.gson.JsonElement
import retrofit2.http.*

/**
 * author : qfxl
 * e-mail : xuyonghong0822@gmail.com
 * time   : 2021/09/20
 * desc   :
 * version: 1.0
 */

interface WanApi {
    @GET("/banner/json")
    suspend fun getBanners(): BaseResponse<List<BannerResponse>>

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(@FieldMap map:Map<String,String>):BaseResponse<JsonElement>

    companion object {
        private val service by lazy {
            NetworkClient.get().create(WanApi::class.java)
        }

        suspend fun getBanners() = service.getBanners()

        suspend fun login() = service.login(mapOf(
            "username" to "xuyonghong",
            "password" to "123456"
        ))
    }
}