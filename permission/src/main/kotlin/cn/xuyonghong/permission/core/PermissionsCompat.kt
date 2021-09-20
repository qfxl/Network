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

import android.Manifest

/**
 * special permissions
 * External storage permissions (special permissions, Android 11 and above are required)
 * If the application needs to be put on GooglePlay, please check:
 * https://support.google.com/googleplay/android-developer/answer/9956427
 */
const val PERMISSION_MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE"

/**
 * special permissions
 * Install application permissions (special permissions, Android 8.0 and above are required)
 * Android 11 feature adjustment, installation of external source applications requires restarting the App:
 * https://news.51cto.com/art/202006/618118.htm
 */
const val PERMISSION_REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"

/**
 * special permissions
 * Floating window permissions (Android 6.0 and above are required.
 * In Android 10 and earlier versions, you can jump to the application floating window setting page,
 * but in Android 11 and later versions, you can only jump to the system setting floating window management list.
 */
const val PERMISSION_SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW

/**
 * special permissions
 * Notification bar permissions (special permissions, Android 8.0 and above are required,
 * note that this permission can be applied without registering in the manifest file)
 */
const val PERMISSION_NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE"

/**
 * special permissions
 * System setting permissions (special permissions, Android 6.0 and above required)
 */
const val PERMISSION_WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS

/**
 * permission for apps that need to detect the user’s steps
 * or classify the user’s physical activity (such as walking, biking, or riding)
 */
const val PERMISSION_ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION"

/**
 * Get location in the background (requires Android 10.0 and above)
 */
const val PERMISSION_ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION"

/**
 * Read geographic location in photos (Requires Android 10.0 and above)
 * If the permission application is successful but the geographic information of the photo cannot be read normally,
 * you need to apply for storage permission first:
 * if targetSdkVersion <= 29 request WRITE、READ_EXTERNAL_STORAGE
 * if targetSdkVersion >= 30 request MANAGE_EXTERNAL_STORAGE
 */
const val PERMISSION_ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION"

/**
 * Allow the calling application to continue the call initiated in another application, Android 9.0 or higher
 */
const val PERMISSION_ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER"

/**
 * Answer calls (Android 8.0 and above are required,
 * and Android 8.0 and below can use simulated headset key events to answer calls, this method does not require permission)
 */
const val PERMISSION_ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS"

/**
 * Read mobile phone number (Requires Android 8.0 and above)
 */
const val PERMISSION_READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS"
