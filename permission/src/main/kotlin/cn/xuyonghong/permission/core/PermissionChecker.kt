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
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker
import cn.xuyonghong.permission.ext.*

/**
 * permission checker, check permission request result, compat for special permissions
 */
object PermissionChecker {

    /**
     * special permission list
     */
    private val specialPermissions by lazy {
        listOf(
            PERMISSION_MANAGE_EXTERNAL_STORAGE,
            PERMISSION_REQUEST_INSTALL_PACKAGES,
            PERMISSION_SYSTEM_ALERT_WINDOW,
            PERMISSION_NOTIFICATION_SERVICE,
            PERMISSION_WRITE_SETTINGS
        )
    }

    /**
     * check whether all permission are granted
     * @param context
     * @param permissions
     * @return
     */
    fun isAllPermissionGranted(context: Context, permissions: Array<out String>): Boolean {
        if (!isAndroid6()) {
            return true
        }
        permissions.forEach {
            if (!isPermissionGranted(context, it)) {
                return false
            }
        }
        return true
    }

    /**
     * check permission is granted or not
     * @param context
     * @param permission
     * @return true granted false otherwise
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (!isAndroid6()) {
            return true
        }
        //Android 11 manage storage permission
        if (PERMISSION_MANAGE_EXTERNAL_STORAGE == permission) {
            return isManageStoragePermissionGranted(context)
        }
        //Android 8.0 check install permission
        if (PERMISSION_REQUEST_INSTALL_PACKAGES == permission) {
            return isInstallPermissionGranted(context)
        }
        //Android6.0 alert window permission
        if (PERMISSION_SYSTEM_ALERT_WINDOW == permission) {
            return isAlertWindowPermissionGranted(context)
        }
        //Notification permission
        if (PERMISSION_NOTIFICATION_SERVICE == permission) {
            return isNotificationPermissionGranted(context)
        }
        //Android6.0 Settings permission
        if (PERMISSION_WRITE_SETTINGS == permission) {
            return isSettingPermissionGranted(context)
        }
        //filter special permission
        if (!isAndroid10()) {
            if (PERMISSION_ACCESS_BACKGROUND_LOCATION == permission) {
                return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
            if (PERMISSION_ACTIVITY_RECOGNITION == permission) {
                return context.checkSelfPermission(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED
            }
            if (PERMISSION_ACCESS_MEDIA_LOCATION == permission) {
                return true
            }
        }
        if (!isAndroid9()) {
            if (PERMISSION_ACCEPT_HANDOVER == permission) {
                return true
            }
        }
        if (!isAndroid8()) {
            if (PERMISSION_ANSWER_PHONE_CALLS == permission) {
                return true
            }
            if (PERMISSION_READ_PHONE_NUMBERS == permission) {
                return context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
            }
        }
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * check permissions contain special permission
     * @param requestPermissions
     */
    fun containsSpecialPermission(requestPermissions: List<String>): Boolean {
        requestPermissions.forEach { permission ->
            if (isSpecialPermission(permission)) {
                return true
            }
        }
        return false
    }

    /**
     * permission is special or not
     * @param permission
     */
    fun isSpecialPermission(permission: String): Boolean =
        specialPermissions.contains(permission)

    /**
     * whether external storage permission is all granted
     * @param context
     */
    fun isManageStoragePermissionGranted(context: Context): Boolean {
        if (isAndroid11()) {
            return Environment.isExternalStorageManager()
        }
        return isAllPermissionGranted(
            context, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    /**
     * whether install permission is granted
     */
    fun isInstallPermissionGranted(context: Context): Boolean {
        return isAndroid8() && context.packageManager.canRequestPackageInstalls()
    }

    /**
     * whether alert window permission is granted
     */
    fun isAlertWindowPermissionGranted(context: Context): Boolean {
        return isAndroid6() && Settings.canDrawOverlays(context)
    }

    /**
     * whether notification permission is granted
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * whether setting permission is granted
     */
    fun isSettingPermissionGranted(context: Context): Boolean {
        return isAndroid6() && Settings.System.canWrite(context)
    }


    /**
     * get manifest register permissions
     * @param context
     */
    fun getManifestRegisterPermissions(context: Context): Array<String>? {
        return try {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            ).requestedPermissions
        } catch (e: Exception) {
            null
        }
    }

    /**
     * check permission is granted or not
     * @param context
     * @param permission
     * @return
     */
    @PermissionChecker.PermissionResult
    fun getPermissionStatus(context: Context, permission: String): Int {
        return if (isPermissionGranted(context, permission)) {
            PackageManager.PERMISSION_GRANTED
        } else {
            PackageManager.PERMISSION_DENIED
        }
    }

    /**
     * get granted permissions from request permissions
     * @param context
     * @param permissions
     * @return grantedPermissions
     */
    fun getGrantedPermissionsFromRequestList(
        context: Context,
        permissions: Array<out String>
    ): ArrayList<String> {
        val grantedList = arrayListOf<String>()
        permissions.forEach { permission ->
            if (isPermissionGranted(context, permission)) {
                grantedList.add(permission)
            }
        }
        return grantedList
    }


    /**
     * get denied permissions from request permissions
     * @param activity
     * @param permissions
     * @param ignorePermanentPermission ignore permanent permission from denied list
     * @return deniedPermissions
     */
    fun getDeniedPermissionsFromRequestList(
        activity: Activity,
        permissions: Array<out String>,
        ignorePermanentPermission: Boolean = false
    ): ArrayList<String> {
        val deniedList = arrayListOf<String>()
        permissions.forEach { permission ->
            if (!isPermissionGranted(activity, permission)) {
                if (ignorePermanentPermission) {
                    if (!isPermissionPermanentDenied(activity, permission)) {
                        deniedList.add(permission)
                    }
                } else {
                    deniedList.add(permission)
                }
            }
        }
        return deniedList
    }

    /**
     * get permanent denied permissions from request permissions
     * @param activity
     * @param permissions
     * @return deniedPermissions
     */
    fun getPermanentDeniedPermissionsFromRequestList(
        activity: Activity,
        permissions: Array<out String>
    ): ArrayList<String> {
        val permanentDeniedList = arrayListOf<String>()
        permissions.forEach { permission ->
            if (isPermissionPermanentDenied(activity, permission)) {
                permanentDeniedList.add(permission)
            }
        }
        return permanentDeniedList
    }

    /**
     * verify permission is permanent denied or not
     * @param activity
     * @param permission
     * @return true permission is permanent denied false otherwise
     */
    fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (!isAndroid6()) {
            return false
        }
        if (isSpecialPermission(permission)) {
            return false
        }
        //Android 10.0 location permission
        if (isAndroid10()) {
            if (PERMISSION_ACCESS_BACKGROUND_LOCATION == permission && !isPermissionGranted(
                    activity,
                    PERMISSION_ACCESS_BACKGROUND_LOCATION
                ) && !isPermissionGranted(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                return !activity.shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
        if (!isAndroid10()) {

            if (PERMISSION_ACCESS_BACKGROUND_LOCATION == permission) {
                return !activity.shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) && !isPermissionGranted(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (PERMISSION_ACTIVITY_RECOGNITION == permission) {
                return !activity.shouldShowRequestPermissionRationale(Manifest.permission.BODY_SENSORS)
                        && !isPermissionGranted(activity, Manifest.permission.BODY_SENSORS)
            }

            if (PERMISSION_ACCESS_MEDIA_LOCATION == permission) {
                return false
            }

        }

        if (!isAndroid9()) {
            if (PERMISSION_ACCEPT_HANDOVER == permission) {
                return false
            }
        }

        if (!isAndroid8()) {
            if (PERMISSION_ANSWER_PHONE_CALLS == permission) {
                return true
            }
            if (PERMISSION_READ_PHONE_NUMBERS == permission) {
                return !activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                        && !isPermissionGranted(activity, Manifest.permission.READ_PHONE_STATE)
            }
        }
        return !activity.shouldShowRequestPermissionRationale(permission)
                && !isPermissionGranted(activity, permission)
    }

    /**
     * optimize deprecated permissions
     * @param permissions
     */
    fun optimizeDeprecatedPermission(permissions: Array<out String>): ArrayList<String> {
        val optimizedArrayList = permissions.toCollection(ArrayList())
        if (optimizedArrayList.contains(PERMISSION_MANAGE_EXTERNAL_STORAGE)) {
            if (optimizedArrayList.contains(Manifest.permission.READ_EXTERNAL_STORAGE) || optimizedArrayList.contains(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                //if manage external storage permission is request, READ、WRITE externalStorage no longer needed
                throw IllegalArgumentException("READ、WRITE_EXTERNAL_STORAGE are no longer needed, if MANAGE_EXTERNAL_STORAGE permission is applied.")
            }

            if (!isAndroid11()) {
                optimizedArrayList.remove(PERMISSION_MANAGE_EXTERNAL_STORAGE)
                //WRITE_EXTERNAL_STORAGE is only support before Android10
                if (!isAndroid10()) {
                    optimizedArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                optimizedArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        //WRITE_EXTERNAL_STORAGE only support before Android10
        if (isAndroid10() && optimizedArrayList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            optimizedArrayList.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!isAndroid10() && optimizedArrayList.contains(PERMISSION_ACTIVITY_RECOGNITION)
            && !optimizedArrayList.contains(Manifest.permission.BODY_SENSORS)
        ) {
            optimizedArrayList.add(Manifest.permission.BODY_SENSORS)
        }

        if (!isAndroid8() && optimizedArrayList.contains(PERMISSION_READ_PHONE_NUMBERS)
            && !optimizedArrayList.contains(Manifest.permission.READ_PHONE_STATE)
        ) {
            optimizedArrayList.add(Manifest.permission.READ_PHONE_STATE)
        }

        return optimizedArrayList
    }
}