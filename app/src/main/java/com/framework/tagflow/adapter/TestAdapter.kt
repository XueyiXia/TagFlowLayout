package com.framework.tagflow.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.framework.tagflow.R
import com.framework.tagflow.bean.SearchHistoryBean


/**
 * @author: xiaxueyi
 * @date: 2022-09-27
 * @time: 14:04
 * @说明:
 */
@SuppressLint("ViewHolder")
class TestAdapter (private val mActivity: Activity) : TagAdapter<SearchHistoryBean?>() {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return LayoutInflater.from(mActivity).inflate(R.layout.layout_tag_item,parent, false)
    }
}