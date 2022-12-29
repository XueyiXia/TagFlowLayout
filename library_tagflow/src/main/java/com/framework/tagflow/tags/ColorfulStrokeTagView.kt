package com.framework.tagflow.tags

import android.content.Context
import android.util.AttributeSet

/**
 * @author: xiaxueyi
 * @date: 2022-09-19
 * @time: 16:02
 * @说明:
 */

class ColorfulStrokeTagView : ColorfulTagView {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun isSolid(): Boolean {
        return false
    }

    override fun getStrokeWidth(): Float {
        return 1F
    }
}