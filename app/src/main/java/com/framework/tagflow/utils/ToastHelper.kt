package com.framework.tagflow.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.framework.tagflow.R


/**
 * @author: xiaxueyi
 * @date: 2022-10-13
 * @time: 11:30
 * @说明: Toast,支持Snackbar
 */
object ToastHelper {
    private var mToast: Toast? = null

    /**
     * 传入ID，默认义Toast 位置为居中
     * @param context
     * @param strRes
     */
    fun showMsgShort(context: Context?, strRes: Int) {
        if (context == null) {
            return
        }
        baseShowMsg(context, context.resources.getString(strRes), 0, Toast.LENGTH_SHORT)
    }

    /**
     * 传入ID
     * @param context
     * @param strRes
     * @param gravity   Toast 位置
     */
    fun showMsgShort(context: Context?, strRes: Int, gravity: Int) {
        if (context == null) {
            return
        }
        baseShowMsg(context, context.resources.getString(strRes), gravity, Toast.LENGTH_SHORT)
    }

    /**
     * 传如字符串，默认义Toast 位置为居中
     * @param context
     * @param strRes
     */
    fun showMsgShort(context: Context?, strRes: String) {
        if (context == null) {
            return
        }
        baseShowMsg(context, strRes, Gravity.CENTER, Toast.LENGTH_SHORT)
    }

    /**
     * 传如字符串
     * @param context
     * @param strRes
     */
    fun showMsgShort(context: Context?, strRes: String, gravity: Int) {
        if (context == null) {
            return
        }
        baseShowMsg(context, strRes, gravity, Toast.LENGTH_SHORT)
    }

    /**
     * 传入id，默认义Toast 位置为居中
     * @param context
     * @param strRes
     */
    fun showMsglong(context: Context?, strRes: Int) {
        if (context == null) {
            return
        }
        baseShowMsg(context, context.resources.getString(strRes), Gravity.CENTER, Toast.LENGTH_LONG)
    }

    /**
     * 传入id ,自定义Toast 位置
     * @param context
     * @param strRes
     * @param gravity
     */
    fun showMsglong(context: Context?, strRes: Int, gravity: Int) {
        if (context == null) {
            return
        }
        baseShowMsg(context, context.resources.getString(strRes), gravity, Toast.LENGTH_LONG)
    }

    /**
     * 传如字符串,默认义Toast 位置为居中
     * @param context
     * @param strRes
     */
    fun showMsglong(context: Context?, strRes: String) {
        if (context == null) {
            return
        }
        baseShowMsg(context, strRes, Gravity.CENTER, Toast.LENGTH_LONG)
    }

    /**
     * 传如字符串,自定义Toast 位置
     * @param context
     * @param strRes
     * @param gravity
     */
    fun showMsglong(context: Context?, strRes: String, gravity: Int) {
        if (context == null) {
            return
        }
        baseShowMsg(context, strRes, gravity, Toast.LENGTH_LONG)
    }

    /**
     * 长时间弹出框
     * @param context 上下文对象
     * @param strRes
     * @param gravity
     * @param duration 参数 duration 用来设置显示时间的长短 Toast.LENGTH_SHORT和Toast.LENGTH_LONG
     */
    private fun baseShowMsg(context: Context?, strRes: String, gravity: Int, duration: Int) {
        if (context == null) {
            return
        }
        /**
         * 兼容安卓11 版本
         */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            mToast = Toast(context)
            val inflater = LayoutInflater.from(context)
            val layout: View = inflater.inflate(com.framework.tagflow.R.layout.custom_toast, null)
            val message = layout.findViewById<View>(R.id.ad_hoc_toast_textview) as TextView
            message.text = strRes
            setToastTextStyle(context, message)
            mToast!!.setView(layout)
            mToast!!.show()
            return
        }
        if (mToast == null) {
            if (duration == Toast.LENGTH_SHORT) {
                mToast = Toast.makeText(context, Html.fromHtml(strRes), Toast.LENGTH_SHORT)
            } else {
                mToast = Toast.makeText(context, Html.fromHtml(strRes), Toast.LENGTH_LONG)
            }
        } else {
            mToast!!.setText(Html.fromHtml(strRes))
        }
        /**
         * 设置Toast 位置
         */
        if (gravity != 0) {
            mToast!!.setGravity(gravity, 0, 20)
        } else {
            mToast!!.setGravity(Gravity.BOTTOM, 0, 20)
        }
        setToastBackground(context)
        mToast!!.show()
    }

    /**
     * 设置Toat背景和字体样式
     * @param context
     */
    private fun setToastBackground(context: Context) {
        val view = mToast!!.view ?: return
        //设置Toast背景颜色为透明
        view.setBackgroundColor(Color.TRANSPARENT)
        val message = view.findViewById<View>(android.R.id.message) as TextView
        setToastTextStyle(context, message)
    }

    /**
     * 设置文本样式
     * @param context
     * @param textView
     */
    private fun setToastTextStyle(context: Context, textView: TextView) {
        textView.setBackgroundResource(R.drawable.toast_background)
        textView.setTextColor(ContextCompat.getColor(context,R.color.white))
        textView.minWidth = 450
        textView.minHeight = 120
        textView.setPadding(20, 0, 20, 0)
        textView.gravity = Gravity.CENTER
    }

    /**
     * 关闭Toast
     */
    fun cancelToast() {
        if (mToast != null) {
            mToast!!.cancel()
        }
    }
}