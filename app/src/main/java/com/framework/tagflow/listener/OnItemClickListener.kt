package com.framework.tagflow.listener

import android.view.View
import com.framework.tagflow.viewholder.BaseQuickAdapter

/**
 * @author: limuyang
 * @date: 2019-12-03
 * @Description: Interface definition for a callback to be invoked when an item in this
 * RecyclerView itemView has been clicked.
 */
interface OnItemClickListener {
    /**
     * Callback method to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param adapter  the adapter
     * @param view     The itemView within the RecyclerView that was clicked (this
     * will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)
}