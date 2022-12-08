package com.framework.tagflow.tags


import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.framework.tagflow.utils.DensityUtils
import com.framework.tagflow.R

/**
 * @author: xiaxueyi
 * @date: 2022-09-16
 * @time: 15:02
 * @说明:
 */

class MutSelectedTagView : DefaultTagView {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun getBackgroundDrawable(): Drawable? {
        //设置字体颜色的选择器
        val colorSateList = ContextCompat.getColorStateList(context,R.color.secondary_text)
        setTextColor(colorSateList)
        val normal = GradientDrawable()
        normal.shape = GradientDrawable.RECTANGLE
        normal.cornerRadius = DensityUtils.dp2px(mContext, getTagRadius()).toFloat()
        if (isSolid()) {
            normal.setColor(getNormalBackgroundColor())
        } else {
            normal.setStroke(
                DensityUtils.dp2px(mContext, getStrokeWidth()),
                getNormalBackgroundColor()
            )
            normal.setColor(getBackgroundColor())
        }
        val pressed = GradientDrawable()
        pressed.shape = GradientDrawable.RECTANGLE
        pressed.cornerRadius = DensityUtils.dp2px(mContext, getTagRadius()).toFloat()
        if (isSolid()) {
            pressed.setColor(getPressedBackgroundColor())
        } else {
            pressed.setStroke(
                DensityUtils.dp2px(mContext, getStrokeWidth()),
                getPressedBackgroundColor()
            )
            pressed.setColor(getPressedBackgroundColor())
        }
        val selector = StateListDrawable()
        selector.addState(intArrayOf(android.R.attr.state_pressed), pressed)
        selector.addState(intArrayOf(android.R.attr.state_selected), pressed)
        selector.addState(intArrayOf(), normal)
        return selector
    }

    fun toggle() {
        this.isSelected = !isSelected
    }
}