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

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.xuyonghong.permission.ext.getScreenWidth
import cn.xuyonghong.permission.request.DialogNegativeCallback
import cn.xuyonghong.permission.request.DialogPositiveCallback


abstract class BasePermissionDialog : DialogFragment() {

    private var onDismissListener: DialogInterface.OnDismissListener? = null

    var canCancel = true

    var positiveCallback: DialogPositiveCallback? = null

    var negativeCallback: DialogNegativeCallback? = null

    private val isShowing: Boolean
        get() = dialog != null && dialog!!.isShowing && !isRemoving

    fun showDialog(activity: FragmentActivity) {
        if (isShowing) {
            dismiss()
        }
        show(activity.supportFragmentManager, javaClass.simpleName)
    }

    fun showDialog(fragment: Fragment) {
        if (isShowing) {
            dismiss()
        }
        show(fragment.childFragmentManager, javaClass.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    abstract fun showDialogWithPermissions(
        activity: FragmentActivity,
        permissions: Array<out String>
    )

    @IdRes
    abstract fun getPositiveViewId(): Int

    @IdRes
    abstract fun getNegativeViewId(): Int

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View?>(getPositiveViewId())?.setOnClickListener { v ->
            positiveCallback?.invoke(v)
            dismissAllowingStateLoss()
        }
        view.findViewById<View?>(getNegativeViewId())?.setOnClickListener { v ->
            negativeCallback?.invoke(v)
            dismissAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.apply {
            window?.apply {
                val lp = attributes
                lp.width = (requireActivity().getScreenWidth() * 0.85).toInt()
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                attributes = lp
                //set background color
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            setCancelable(canCancel)
            setCanceledOnTouchOutside(canCancel)
        }
    }


    fun registerDismissListener(dismissAction: () -> Unit) {
        onDismissListener = DialogInterface.OnDismissListener { dismissAction() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }
}