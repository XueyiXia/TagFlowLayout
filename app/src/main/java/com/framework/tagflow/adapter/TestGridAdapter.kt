package com.framework.tagflow.adapter

import android.widget.TextView
import com.framework.tagflow.bean.TestBean
import com.framework.tagflow.viewholder.BaseQuickAdapter
import com.framework.tagflow.viewholder.BaseViewHolder
import com.tagflow.R

class TestGridAdapter : BaseQuickAdapter<TestBean, BaseViewHolder>(R.layout.layout_grid_item) {

    override fun convert(holder: BaseViewHolder, item: TestBean) {

        val name:TextView=holder.getView(R.id.name)
        val num:TextView=holder.getView(R.id.num)

        name.text=item.getTitle()
        num.text=item.getId().toString()
    }
}