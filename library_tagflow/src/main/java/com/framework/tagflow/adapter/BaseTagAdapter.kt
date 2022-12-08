package com.framework.tagflow.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.framework.tagflow.bean.BaseTagBean


abstract class BaseTagAdapter<T> : BaseAdapter() {

    private var dataList: MutableList<T> = mutableListOf()


    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): T {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    abstract override fun getView(position: Int, convertView: View?, parent: ViewGroup): View


    fun getData(): List<T> {
        return dataList
    }

    /**
     * 增加一条数据
     * @param dataList T
     */
    fun addData(dataList: T) {
        this.dataList.add(dataList)
        notifyDataSetChanged()
    }

    /**
     * 增加列表
     * @param dataList List<T>?
     */
    fun addAllList(dataList: List<T>?) {
        this.dataList.clear()
        if (null != dataList) {
            this.dataList.addAll(dataList)
        }
        notifyDataSetChanged()
    }

    /**
     *
     */
    open fun select(position: Int) {
        if (position < 0 || position >= dataList.size) {
            return
        }
        if(dataList[position] is BaseTagBean){
            (dataList[position] as BaseTagBean).setIsSelected(true)
        }
    }

    /**
     *
     */
    open fun unSelect(position: Int) {
        if (position < 0 || position >= dataList.size) {
            return
        }
        if(dataList[position] is BaseTagBean){
           (dataList[position] as BaseTagBean).setIsSelected(false)
        }

    }
}