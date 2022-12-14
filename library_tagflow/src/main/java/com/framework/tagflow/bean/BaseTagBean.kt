package com.framework.tagflow.bean
/**
 * @author: xiaxueyi
 * @date: 2022-09-16
 * @time: 16:02
 * @说明:
 */

open class BaseTagBean {
    private var id = 0
    private var title: String? = null
    private var isSelected = false//是否选中
    private var tag: Any? = null //标签额外信息

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getTag(): Any? {
        return tag
    }

    fun setTag(tag: Any?) {
        this.tag = tag
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setIsSelected(selected: Boolean) {
        this.isSelected = selected
    }
}