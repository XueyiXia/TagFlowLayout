package com.framework.tagflow.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.framework.tagflow.bean.BaseTagBean
import com.framework.tagflow.bean.TestBean
import com.framework.tagflow.tags.DefaultTagView
import com.framework.tagflow.tags.MutSelectedTagView
import com.tagflow.R


/**
 * @author: xiaxueyi
 * @date: 2022-09-27
 * @time: 14:04
 * @说明:
 */
@SuppressLint("ViewHolder")
class TestAdapter (private val mActivity: Activity,var isHint:Boolean) : BaseTagAdapter<TestBean?>() {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        if(isHint){
            val textView: DefaultTagView = MutSelectedTagView(mActivity)
            textView.text = (getItem(position) as BaseTagBean?)?.getTitle() ?: "add tag$position"
            return textView
        }else{
            val view= LayoutInflater.from(mActivity).inflate(R.layout.layout_tag_item,parent, false)
            val textView: TextView=view.findViewById<TextView>(R.id.stock_name)
            textView.text = (getItem(position) as BaseTagBean?)?.getTitle() ?: "tag$position"
            return view
        }


    }
}