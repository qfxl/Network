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

package cn.xuyonghong.permission.dialog

import androidx.fragment.app.FragmentActivity
import cn.xuyonghong.permission.R
import cn.xuyonghong.permission.ext.openSetting

class DefaultPermanentDeniedDialog : BasePermissionDialog() {

    init {
        canCancel = false
        positiveCallback = {
            requireActivity().openSetting()
        }
    }

    override fun showDialogWithPermissions(
        activity: FragmentActivity,
        permissions: Array<out String>
    ) {
        showDialog(activity)
    }

    override fun getPositiveViewId(): Int = R.id.tv_default_permanent_denied_dialog_confirm

    override fun getNegativeViewId(): Int = R.id.tv_default_permanent_denied_dialog_cancel

    override fun getLayoutId(): Int = R.layout.fragment_default_permanent_denied_dialog
}