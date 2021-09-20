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

package cn.xuyonghong.permission.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics

/**
 * runtime permission min sdk，Android6.0
 */
const val ANDROID_6 = Build.VERSION_CODES.M

/**
 * Android 7.0
 */
const val ANDROID_7 = Build.VERSION_CODES.N

/**
 * Android 8.0
 */
const val ANDROID_8 = Build.VERSION_CODES.O

/**
 * Android 9.0
 */
const val ANDROID_9 = Build.VERSION_CODES.P

/**
 * Android 10.0
 */
const val ANDROID_10 = Build.VERSION_CODES.Q

/**
 * Android 11.0
 */
const val ANDROID_11 = Build.VERSION_CODES.R

/**
 * Android 12.0
 */
const val ANDROID_12 = Build.VERSION_CODES.S

fun isAndroid6() = Build.VERSION.SDK_INT >= ANDROID_6

fun isAndroid7() = Build.VERSION.SDK_INT >= ANDROID_7

fun isAndroid8() = Build.VERSION.SDK_INT >= ANDROID_8

fun isAndroid9() = Build.VERSION.SDK_INT >= ANDROID_9

fun isAndroid10() = Build.VERSION.SDK_INT >= ANDROID_10

fun isAndroid11() = Build.VERSION.SDK_INT >= ANDROID_11

fun isAndroid12() = Build.VERSION.SDK_INT >= ANDROID_12

fun isSdkGreaterOrEqual(targetSdkInt: Int) = Build.VERSION.SDK_INT >= targetSdkInt

fun Activity.getScreenWidth(): Int {
    val windowManager = windowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.getPackageUri() = Uri.parse("package:$packageName")

fun Context.openSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = getPackageUri()
    startActivity(intent)
}