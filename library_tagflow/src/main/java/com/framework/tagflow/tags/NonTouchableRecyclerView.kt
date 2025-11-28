package com.framework.tagflow.tags

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NonTouchableRecyclerView(context: Context) : RecyclerView (context){

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        // 拦截触摸事件，但允许点击事件传递
        return false // 返回 false 允许触摸事件传递给子 View (例如 Item 的点击事件)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        // 阻止 RecyclerView 自己滚动
        return false // 返回 false 阻止 RecyclerView 处理滚动事件
    }

    override fun canScrollVertically(direction: Int): Boolean {
        // 禁止垂直滑动
        return false // 返回 false 禁止垂直滚动
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        // 禁止水平滑动
        return false // 返回 false 禁止水平滚动
    }
}
