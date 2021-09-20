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
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.xuyonghong.permission.dialog.BasePermissionDialog
import cn.xuyonghong.permission.ext.*
import cn.xuyonghong.permission.request.PermissionCallback

/**
 * class for permission request, all request logic
 */
class PermissionFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 0x01
        private const val REQUEST_CODE_ACTIVITY_RESULT = 0x02
        private const val REQUEST_CODE_PERMISSION_LOCATION = 0x03
        private const val ARGS_PERMISSIONS = "permissionArgs"

        /**
         * new instance
         * @param permissions
         */
        fun newInstance(permissions: ArrayList<String>): PermissionFragment {
            return PermissionFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARGS_PERMISSIONS, permissions)
                }
            }
        }
    }

    init {
        retainInstance = true
    }

    /**
     * permission callback
     */
    private var permissionCallback: PermissionCallback? = null

    /**
     * request rational dialog
     */
    private var mRationalDialog: BasePermissionDialog? = null

    /**
     * show permanent denied dialog
     */
    private var mPermanentDeniedDialog: BasePermissionDialog? = null

    /**
     * requested permissions
     */
    private val requestedPermissions by lazy {
        arguments?.getStringArrayList(ARGS_PERMISSIONS) ?: arrayListOf()
    }

    /**
     * determine fragment usage
     */
    var isForPermissionRequest = false

    /**
     * permission is requested or not
     */
    private var isAlreadyRequested = false

    /**
     * permission request runnable
     */
    private val requestRunnable by lazy {
        Runnable {
            if (isAdded) {
                requestNormalPermissions()
            }
        }
    }

    /**
     * start request unCategorizedPermissions
     * determine whether special permission contains
     */
    private fun requestUnCategorizedPermissions() {
        //flag special permission contains or not
        var containsSpecialPermissions = false
        if (PermissionChecker.containsSpecialPermission(requestedPermissions)) {
           //Android 11.0 manage external storage permission
           if (requestedPermissions.contains(PERMISSION_MANAGE_EXTERNAL_STORAGE) && !PermissionChecker.isManageStoragePermissionGranted(
                   requireActivity()
               )
           ) {
               if (isAndroid11()) {
                   startActivityForResult(
                       IntentCreator.createStorageManageIntent(requireActivity()),
                       REQUEST_CODE_ACTIVITY_RESULT
                   )
                   containsSpecialPermissions = true
               }
           }
           //Android 8.0 apk install permission
           if (requestedPermissions.contains(PERMISSION_REQUEST_INSTALL_PACKAGES) && !PermissionChecker.isInstallPermissionGranted(
                   requireActivity()
               )
           ) {
               if (isAndroid8()) {
                   startActivityForResult(
                       IntentCreator.createInstallManageIntent(requireActivity()),
                       REQUEST_CODE_ACTIVITY_RESULT
                   )
                   containsSpecialPermissions = true
               }
           }
           //Android 6.0 alert window permission
           if (requestedPermissions.contains(PERMISSION_SYSTEM_ALERT_WINDOW) && !PermissionChecker.isAlertWindowPermissionGranted(
                   requireActivity()
               )
           ) {
               if (isAndroid6()) {
                   startActivityForResult(
                       IntentCreator.createAlertWindowManageIntent(requireActivity()),
                       REQUEST_CODE_ACTIVITY_RESULT
                   )
                   containsSpecialPermissions = true
               }
           }
           //Android 8.0 Notification bar permission
           if (requestedPermissions.contains(PERMISSION_NOTIFICATION_SERVICE) && !PermissionChecker.isNotificationPermissionGranted(
                   requireActivity()
               )
           ) {
               if (isAndroid8()) {
                   startActivityForResult(
                       IntentCreator.createNotificationManageIntent(requireActivity()),
                       REQUEST_CODE_ACTIVITY_RESULT
                   )
                   containsSpecialPermissions = true
               }
           }
           //Android 6.0 write settings permission
           if (requestedPermissions.contains(PERMISSION_WRITE_SETTINGS) && !PermissionChecker.isSettingPermissionGranted(
                   requireActivity()
               )
           ) {
               if (isAndroid6()) {
                   startActivityForResult(
                       IntentCreator.createSettingManageIntent(requireActivity()),
                       REQUEST_CODE_ACTIVITY_RESULT
                   )
                   containsSpecialPermissions = true
               }
           }
       }
        if (!containsSpecialPermissions) {
            requestNormalPermissions()
        }
    }

    /**
     * request normal permissions
     */
    private fun requestNormalPermissions() {
        // normal permission request
        // warn: The Android 10 positioning strategy has changed.
        // The prerequisite for applying for background positioning permission is to have the foreground positioning permission
        // (either precise or fuzzy permission is granted)
        val locationPermissionList = arrayListOf<String>()
        if (isAndroid10() && requestedPermissions.contains(PERMISSION_ACCESS_BACKGROUND_LOCATION)) {
            // Provide an estimate of the location of the device,
            // limiting the range to approximately 1.6 kilometers (1 mile).
            if (requestedPermissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                locationPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            // Provides a small precision device location value,
            // usually limited to about 50 meters (160 feet),
            // and sometimes accurate to within a few meters (10 feet).
            if (requestedPermissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                locationPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            // before Android10 , when foreground is granted , background permission is granted also.
        }

        if (locationPermissionList.isEmpty()) {
            requestPermissions(requestedPermissions.toTypedArray(), REQUEST_CODE_PERMISSION)
        } else {
            // For devices with Android 10 or higher,
            // first request the foreground location permission
            // and then request the background location permission
            // first step request foreground location permission
            requestPermissions(
                locationPermissionList.toTypedArray(),
                REQUEST_CODE_PERMISSION_LOCATION
            )
        }
    }

    /**
     * add PermissionFragment to activity host
     * @param attachedActivity host
     */
    private fun attachToActivity(attachedActivity: FragmentActivity) {
        attachedActivity.supportFragmentManager.beginTransaction()
            .add(this, hashCode().toString())
            .commitAllowingStateLoss()
    }

    /**
     * remove PermissionFragment from activity host
     */
    private fun detachFromActivity() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this@PermissionFragment)
            .commitAllowingStateLoss()
    }

    /**
     * Callback for the result from requesting permissions.
     * This method is invoked for every call on requestPermissions(String[], int).
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val showRationalPermissionList = arrayListOf<String>()
        for (i in permissions.indices) {
            val permission = permissions[i]
            // Check the grantResults of special permissions
            if (PermissionChecker.isSpecialPermission(permission)) {
                grantResults[i] =
                    PermissionChecker.getPermissionStatus(requireActivity(), permission)
                continue
            }

            // Check 3 new permissions for Android 10.0
            if (!isAndroid10() && (PERMISSION_ACCESS_BACKGROUND_LOCATION == permission || PERMISSION_ACTIVITY_RECOGNITION == permission || PERMISSION_ACCESS_MEDIA_LOCATION == permission)) {
                // If the current version does not meet the minimum requirements, then re-check permissions
                grantResults[i] =
                    PermissionChecker.getPermissionStatus(requireActivity(), permission)
                continue
            }
            // Recheck 1 new permission of Android9.0
            if (!isAndroid9() && PERMISSION_ACCEPT_HANDOVER == permission) {
                // If the current version does not meet the minimum requirements, then re-check permissions
                grantResults[i] =
                    PermissionChecker.getPermissionStatus(requireActivity(), permission)
                continue
            }
            // Recheck the 2 permissions of Android8.0
            if (!isAndroid8() && (PERMISSION_ANSWER_PHONE_CALLS == permission || PERMISSION_READ_PHONE_NUMBERS == permission)) {
                // If the current version does not meet the minimum requirements, then re-check permissions
                grantResults[i] =
                    PermissionChecker.getPermissionStatus(requireActivity(), permission)
                continue
            }
            if (shouldShowRequestPermissionRationale(permission)) {
                showRationalPermissionList.add(permission)
            }
        }
        // granted permissions
        val grantedPermissions =
            PermissionChecker.getGrantedPermissionsFromRequestList(requireActivity(), permissions)
        // denied permissions contains permanent denied permissions
        val allDeniedPermissions =
            PermissionChecker.getDeniedPermissionsFromRequestList(requireActivity(), permissions)
        // permanent denied permissions
        val permanentDeniedPermissions =
            PermissionChecker.getPermanentDeniedPermissionsFromRequestList(
                requireActivity(),
                permissions
            )
        // show permission rational dialog
        if (showRationalPermissionList.isNotEmpty()) {
            mRationalDialog?.showDialogWithPermissions(
                requireActivity(),
                showRationalPermissionList.toTypedArray()
            )
        } else {
            // show permanent denied dialog
            if (permanentDeniedPermissions.isNotEmpty()) {
                mPermanentDeniedDialog?.showDialogWithPermissions(
                    requireActivity(),
                    permanentDeniedPermissions.toTypedArray()
                )
            }
        }
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                permissionCallback?.invoke(
                    grantedPermissions.size == permissions.size,
                    allDeniedPermissions
                )
                detachFromActivity()
            }
            REQUEST_CODE_PERMISSION_LOCATION -> {
                // deal with foreground location permission
                if (grantedPermissions.size == permissions.size) {
                    //request background permission
                    requestPermissions(
                        arrayOf(PERMISSION_ACCESS_BACKGROUND_LOCATION),
                        REQUEST_CODE_PERMISSION
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isForPermissionRequest) {
            if (!isAlreadyRequested) {
                requestUnCategorizedPermissions()
            }
            isAlreadyRequested = true
        } else {
            detachFromActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (activity == null || arguments == null) {
            return
        }
        isAlreadyRequested = true
        activity?.window?.decorView?.postDelayed(requestRunnable, 300)
    }

    override fun onDestroy() {
        activity?.window?.decorView?.removeCallbacks(requestRunnable)
        super.onDestroy()
    }

    /**
     * setup rational dialog
     * @param rationalDialog
     */
    fun setUpRationalDialog(rationalDialog: BasePermissionDialog) {
        mRationalDialog = rationalDialog
    }

    /**
     * setup permanent dialog
     * @param permanentDialog
     */
    fun setUpPermanentDialog(permanentDialog: BasePermissionDialog) {
        mPermanentDeniedDialog = permanentDialog
    }

    /**
     * start request
     */
    fun request(attachedActivity: FragmentActivity, callback: PermissionCallback) {
        permissionCallback = callback
        isForPermissionRequest = true
        isAlreadyRequested = false
        mRationalDialog?.positiveCallback = {
            request(attachedActivity, callback)
        }
        attachToActivity(attachedActivity)
    }
}