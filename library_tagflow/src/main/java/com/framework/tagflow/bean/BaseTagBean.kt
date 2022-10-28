package com.aa.cat.ui.view.tagFlow.bean
/**
 * @author: xiaxueyi
 * @date: 2022-09-16
 * @time: 16:02
 * @说明:
 */

open class BaseTagBean {
    private var id = 0
    private var name: String? = null
    private var selected = false//是否选中
    private var tag: Any? = null //标签额外信息

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getTag(): Any? {
        return tag
    }

    fun setTag(tag: Any?) {
        this.tag = tag
    }

    fun isSelected(): Boolean {
        return selected
    }

    fun setSelected(selected: Boolean) {
        this.selected = selected
    }
}