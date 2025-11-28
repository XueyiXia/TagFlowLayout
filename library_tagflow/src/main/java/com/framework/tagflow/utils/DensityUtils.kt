package com.framework.tagflow.utils

import android.content.Context
import android.util.TypedValue

/**
 * @author: xiaxueyi
 * @date: 2022-09-19
 * @time: 13:02
 * @说明:
 */
class DensityUtils private constructor() {
    companion object {
        /**
         * dp转px
         *
         * @param context 上下文
         * @param dpVal dp值
         * @return result in px
         */
        fun dp2px(context: Context, dpVal: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpVal * scale+0.5F).toInt()
        }

        /**
         * sp转px
         *
         * @param context 上下文
         * @param spVal sp值
         * @return result in px
         */
        fun sp2px(context: Context, spVal: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                spVal, context.resources.displayMetrics
            ).toInt()
        }

        /**
         * px转dp
         *
         * @param context 上下文
         * @param pxVal px值
         * @return result in dp
         */
        fun px2dp(context: Context, pxVal: Float): Float {
            val scale = context.resources.displayMetrics.density
            return pxVal / scale
        }

        /**
         * px转sp
         *
         * @param context 上下文
         * @param pxVal px值
         * @return result in sp
         */
        fun px2sp(context: Context, pxVal: Float): Float {
            return pxVal / context.resources.displayMetrics.density
        }
    }

    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }
}