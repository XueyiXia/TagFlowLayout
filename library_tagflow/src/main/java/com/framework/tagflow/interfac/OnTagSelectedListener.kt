package com.framework.tagflow.interfac

import android.view.View
import com.aa.cat.ui.view.tagFlow.bean.BaseTagBean

/**
 * @author: xiaxueyi
 * @date: 2022-09-13
 * @time: 15:30
 * @说明:标签被选中或取消选中监听
 */
interface OnTagSelectedListener {
    fun selected(view: View?, position: Int, selected: List<BaseTagBean?>?)
    fun unSelected(view: View?, position: Int, selected: List<BaseTagBean?>?)
}