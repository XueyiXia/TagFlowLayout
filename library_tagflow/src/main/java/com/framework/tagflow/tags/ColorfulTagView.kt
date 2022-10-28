package com.framework.tagflow.tags

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import java.util.*

/**
 * @author: xiaxueyi
 * @date: 2022-09-16
 * @time: 16:02
 * @说明:
 */

open class ColorfulTagView : DefaultTagView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun getNormalBackgroundColor(): Int {
        val random = Random()
        val red = random.nextInt(200) + 50
        val green = random.nextInt(200) + 50
        val blue = random.nextInt(200) + 50
        return Color.rgb(red, green, blue)
    }
}