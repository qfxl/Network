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

package cn.xuyonghong.permission.core

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import cn.xuyonghong.permission.ext.*

/**
 * author : qfxl
 * e-mail : xuyonghong0822@gmail.com
 * time   : 2021/07/09
 * desc   :
 * version: 1.0
 */

object IntentCreator {
    /**
     * create storage manage intent
     * @param context
     * @return storage manage intent or appDetailIntent if storage mamge intent is invalid
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun createStorageManageIntent(context: Context): Intent {
        return getSpecialPermissionIntent(
            context,
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            ANDROID_11
        )
    }

    /**
     * create apk install manage intent
     * @param context
     * @return apk install manage intent or appDetailIntent if apk install manage intent is invalid
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createInstallManageIntent(context: Context): Intent {
        return getSpecialPermissionIntent(
            context,
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            ANDROID_8
        )
    }

    /**
     * create alertWindow manage intent
     * @param context
     * @return alertWindow manage intent or appDetailIntent if alertWindow manage intent is invalid
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun createAlertWindowManageIntent(context: Context): Intent {
        return getSpecialPermissionIntent(
            context,
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            ANDROID_6
        )
    }

    /**
     * create notification manage intent
     * @param context
     * @return notification manage intent or appDetailIntent if notification manage intent is invalid
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationManageIntent(context: Context): Intent {
        return getSpecialPermissionIntent(
            context,
            Settings.ACTION_APP_NOTIFICATION_SETTINGS,
            ANDROID_8,
            intentScope = {
                it.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            })
    }

    /**
     * create setting manage intent
     * @param context
     * @return setting manage intent or appDetailIntent if setting manage intent is invalid
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun createSettingManageIntent(context: Context): Intent {
        return getSpecialPermissionIntent(
            context,
            Settings.ACTION_MANAGE_WRITE_SETTINGS,
            ANDROID_6
        )
    }

    /**
     * create special permission intent, for open corresponding detail
     * @param context
     * @param action intent action
     * @param minSdk
     */
    private fun getSpecialPermissionIntent(
        context: Context,
        action: String,
        minSdk: Int,
        intentScope: (Intent) -> Unit = {
            it.data = context.getPackageUri()
        }
    ): Intent {
        var specialPermissionIntent: Intent? = null
        if (isSdkGreaterOrEqual(minSdk)) {
            specialPermissionIntent = Intent(action).apply {
                intentScope(this)
            }
        }
        if (specialPermissionIntent == null) {
            specialPermissionIntent = createAppDetailIntent(context)
        }
        return specialPermissionIntent
    }


    /**
     * for app detail intent
     * @param context
     */
    fun createAppDetailIntent(context: Context) =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = context.getPackageUri()
        }

}