package com.framework.tagflow.tags

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.framework.tagflow.utils.DensityUtils
import com.framework.tagflow.R

/**
 * @author: xiaxueyi
 * @date: 2022-09-16
 * @time: 16:02
 * @说明:
 */

open class DefaultTagView @JvmOverloads constructor(
    protected var mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(
    mContext, attrs, defStyleAttr
) {

    //默认标签内间距
    private val DEFAULT_TAG_PADDING = 5f

    //默认标签背景圆角大小
    private val DEFAULT_TAG_CORNER = 3f

    //默认边框宽度(仅在非实心背景有效)，单位dp
    private val DEFAULT_TAG_STROKE = 1f

    //默认标签背景正常颜色
    private val DEFAULT_TAG_BACKGROUND_COLOR = -0x1

    //默认标签背景按下颜色
    private val DEFAULT_TAG_BACKGROUND_PRESSED_COLOR = -0x68bba4

    private fun init() {
        val padding: Int = DensityUtils.dp2px(mContext, getTagPadding())
        setPadding(padding, padding, padding, padding)
        gravity = Gravity.CENTER
        setBackgroundDrawable(getBackgroundDrawable())
    }//设置字体颜色的选择器

    //完全自定义tag的样式请重写此方法
    //返回一个Drawable背景样式
    protected open fun getBackgroundDrawable(): Drawable? {
        //设置字体颜色的选择器
        val colorSateList = mContext.resources.getColorStateList(R.color.secondary_text)
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
        selector.addState(intArrayOf(), normal)
        return selector
    }



    //默认tag背景颜色
    @ColorInt
    protected open fun getNormalBackgroundColor(): Int {
        return DEFAULT_TAG_BACKGROUND_COLOR
    }

    //按下tag时背景颜色
    @ColorInt
    protected open fun getPressedBackgroundColor(): Int {
        return DEFAULT_TAG_BACKGROUND_PRESSED_COLOR
    }

    //tag的圆角角度
    protected open fun getTagRadius(): Float {
        return DEFAULT_TAG_CORNER
    }

    //tag内间距
    protected open fun getTagPadding(): Float {
        return DEFAULT_TAG_PADDING
    }

    /***
     * 是否为实心背景
     *
     * @return true:实心的，false：空心，可通过[.getStrokeWidth]设置边框宽度
     */
    protected open fun isSolid(): Boolean {
        return true
    }

    //设置边框的宽度，单位dp
    protected open fun getStrokeWidth(): Float {
        return DEFAULT_TAG_STROKE
    }

    //当设置了空心加边框的tag时给tag加一个背景，
    //防止在透明背景上无法显示点击颜色
    @ColorInt
    protected open fun getBackgroundColor(): Int {
        return DEFAULT_TAG_BACKGROUND_COLOR
    }

    init {
        init()
    }
}