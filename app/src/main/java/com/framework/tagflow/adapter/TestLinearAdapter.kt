package com.framework.tagflow.adapter

import android.widget.TextView
import com.framework.tagflow.bean.TestBean
import com.framework.tagflow.viewholder.BaseQuickAdapter
import com.framework.tagflow.viewholder.BaseViewHolder
import com.tagflow.R

class TestLinearAdapter : BaseQuickAdapter<TestBean, BaseViewHolder>(R.layout.layout_linear_item) {

    override fun convert(holder: BaseViewHolder, item: TestBean) {
        holder.getView<TextView>(R.id.name).text = item.getTitle()
    }
}