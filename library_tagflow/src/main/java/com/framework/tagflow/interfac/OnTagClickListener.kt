package com.framework.tagflow.interfac

import android.view.View

/**
 * @author: xiaxueyi
 * @date: 2022-09-13
 * @time: 15:30
 * @说明:
 */
interface OnTagClickListener {
    fun onClick(view: View?, position: Int)
    fun onLongClick(view: View?, position: Int)
}