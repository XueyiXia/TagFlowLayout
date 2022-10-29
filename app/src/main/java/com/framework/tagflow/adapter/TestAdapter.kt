package com.framework.tagflow.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.framework.tagflow.bean.BaseTagBean
import com.framework.tagflow.bean.SearchHistoryBean
import com.framework.tagflow.tags.DefaultTagView
import com.framework.tagflow.tags.MutSelectedTagView


/**
 * @author: xiaxueyi
 * @date: 2022-09-27
 * @time: 14:04
 * @说明:
 */
@SuppressLint("ViewHolder")
class TestAdapter (private val mActivity: Activity) : BaseTagAdapter<SearchHistoryBean?>() {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val textView: DefaultTagView = MutSelectedTagView(mActivity)
        textView.text = (getItem(position) as BaseTagBean?)?.getName() ?: "add tag"
        return textView
//        return LayoutInflater.from(mActivity).inflate(R.layout.layout_tag_item,parent, false)
    }
}