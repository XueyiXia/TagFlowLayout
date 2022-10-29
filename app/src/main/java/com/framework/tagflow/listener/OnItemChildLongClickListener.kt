package com.framework.tagflow.listener

import android.view.View
import com.framework.tagflow.viewholder.BaseQuickAdapter

/**
 * @author: limuyang
 * @date: 2019-12-03
 * @Description:
 */
interface OnItemChildLongClickListener {
    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     *
     * @param adapter  this BaseQuickAdapter adapter
     * @param view     The childView whihin the itemView that was clicked and held.
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int): Boolean
}