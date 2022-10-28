package com.aa.cat.ui.view.tagFlow.tags

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.framework.tagflow.tags.DefaultTagView

/**
 * @author: xiaxueyi
 * @date: 2022-09-16
 * @time: 11:02
 * @说明:
 */
class StrokeTagView : DefaultTagView {
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

    override fun getNormalBackgroundColor(): Int {
        return Color.parseColor("#000000")
    }

    override fun getPressedBackgroundColor(): Int {
        return Color.parseColor("#aa666666")
    }
}