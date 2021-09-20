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

package cn.xuyonghong.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.xuyonghong.permission.request.PermissionRequest

/**
 * permission request flow
 * https://developer.android.com/training/permissions/requesting?hl=zh-cn
 */

class Permission(private val activity: FragmentActivity) {

    companion object {
        fun with(activity: FragmentActivity): Permission {
            return Permission(activity)
        }

        fun with(fragment: Fragment): Permission {
            return Permission(fragment.requireActivity())
        }
    }

    /**
     * setup permissions
     */
    fun permissions(vararg permissions: String): PermissionRequest {
        return PermissionRequest(activity, permissions)
    }

}