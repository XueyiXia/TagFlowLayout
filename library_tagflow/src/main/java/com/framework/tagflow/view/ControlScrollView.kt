package com.framework.tagflow.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * 可控制滑动，可控制固定
 */
class ControlScrollView : ScrollView {
    private var forceFixed = false//强制固定

    private var canScroll = false

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    /**
     * 是否可以滑动
     * @return
     */
    fun isCanScroll(): Boolean {
        return canScroll
    }

    /**
     * 设置是否可以滑动
     * @param isCan
     */
    fun setCanScroll(isCan: Boolean) {
        canScroll = isCan
    }

    /**
     * 是否强制固定
     * @return
     */
    fun isForceFixed(): Boolean {
        return forceFixed
    }

    /**
     * 设置是否强制固定
     * @param forceFixed
     */
    fun setForceFixed(forceFixed: Boolean) {
        this.forceFixed = forceFixed
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (!forceFixed && canScroll) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.dispatchTouchEvent(event)
    }
}