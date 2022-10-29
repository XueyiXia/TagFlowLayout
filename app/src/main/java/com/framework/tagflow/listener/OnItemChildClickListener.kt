package com.framework.tagflow.listener

import android.view.View
import com.framework.tagflow.viewholder.BaseQuickAdapter

/**
 * @author: limuyang
 * @date: 2019-12-03
 * @Description:
 */
interface OnItemChildClickListener {
    /**
     * callback method to be invoked when an item child in this view has been click
     *
     * @param adapter  BaseQuickAdapter
     * @param view     The view whihin the ItemView that was clicked
     * @param position The position of the view int the adapter
     */
    fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)
}