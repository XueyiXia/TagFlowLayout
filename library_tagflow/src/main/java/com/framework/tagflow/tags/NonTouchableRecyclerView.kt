package com.framework.tagflow.tags

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NonTouchableRecyclerView(context: Context) : RecyclerView (context){

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        // 拦截触摸事件，阻止其向下传递
        return true
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        // 处理触摸事件
        return false // 如果不需要处理事件，也可以直接返回 false
    }

    override fun canScrollVertically(direction: Int): Boolean {
        // 禁止垂直滑动
        return false
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        // 禁止水平滑动
        return false
    }
}
