package com.framework.tagflow.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.framework.tagflow.utils.DensityUtils

open class FlowLayout : ViewGroup {

    private var mContext: Context? = null

    private var mLines: MutableList<Line> = ArrayList()     // 记录当前有多少行

    private var horizontalSpace = 0

    private var verticalSpace = 0

    private var mDefaultViewMaxWidth:Int=100  //子view默认宽度的最大值

    private var mDefaultColumn:Int=3;//默认列数三列

    private var mAverageWidth:Int=0 // 存储屏幕的1/3宽度

    private var isNeedMinimumWidth:Boolean=true //是否需要最小宽度值


    /**
     *
     * @param context Context?
     * @constructor
     */
    constructor(context: Context?) : super(context) {
        mContext = context
    }

    /**
     *
     * @param context Context?
     * @param attrs AttributeSet?
     * @constructor
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}


    /**
     *
     * @param horizontalSpace Int
     * @param verticalSpace Int
     */
    fun setSpace(horizontalSpace: Int, verticalSpace: Int) {
        this.horizontalSpace = horizontalSpace
        this.verticalSpace = verticalSpace
    }

    /**
     * 获取单行的高度
     * @return
     */
    fun getLineHeight(): Int {
        return if (mLines.size > 0) {
            mLines[0].height
        } else{
            0
        }
    }

    /**
     * 获取行数
     * @return
     */
    fun getLineSize(): Int {
        return if (mLines.size > 0) {
            mLines.size
        } else {
            0
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = this.paddingTop
        for (i in mLines.indices) {
            val line = mLines[i]

            // 自己布局
            line.layout(top, this.paddingStart)
            top += line.height
            if (i != mLines.size - 1) {
                top += verticalSpace
            }
        }
    }


    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 清空
        mLines.clear()
        var mCurrentLine: Line? = null

        // 获得容器的宽度
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        //获取最大的宽
        val maxWidth = widthSize - this.paddingStart - this.paddingEnd
        //子类的数量
        val childCount = this.childCount

        //计算屏幕的三分之一宽度
        mAverageWidth=maxWidth/mDefaultColumn


        for (i in 0 until childCount) {

            val view = getChildAt(i)

            if (view.visibility == GONE) {
                continue
            }

            if(isNeedMinimumWidth){
                val min = widthSize.coerceAtMost(DensityUtils.dp2px(context, mDefaultViewMaxWidth.toFloat()))
                view.minimumWidth=min
            }

            // 测量孩子
            measureChild(view, widthMeasureSpec, heightMeasureSpec)

            // 将孩子记录到行中
            if (mCurrentLine == null) {
                // 新建行
                mCurrentLine = Line(maxWidth, horizontalSpace)

                // 记录行
                mLines.add(mCurrentLine)

                // 给行添加孩子
                mCurrentLine.addView(view)

            } else {
                // 判断是否可以添加
                if (mCurrentLine.canAdd(view)) {
                    // 可以添加
                    mCurrentLine.addView(view)

                } else {
                    // 不可以添加
                    // 新建一行
                    mCurrentLine = Line(maxWidth, horizontalSpace)
                    // 记录行
                    mLines.add(mCurrentLine)

                    // 给行添加孩子
                    mCurrentLine.addView(view)
                }
            }
        }

        // 设置自己的宽度和高度
        var measuredHeight =  this.paddingTop +  this.paddingBottom

        // 添加行的高度
        for (i in mLines.indices) {
            measuredHeight += mLines[i].height
        }

        // 间距
        measuredHeight += (mLines.size - 1) * verticalSpace

        // 设置自己的宽度和高度
        setMeasuredDimension(widthSize, measuredHeight)
    }




    private inner class Line(
        private var maxWidth: Int,// 行最大宽度
        private var space: Int // view和view之间的间隔
    ) {
        // 属性，方法，构造
        // 行中的控件
        private val mViews: MutableList<View> = ArrayList()
        private var usedWidth = 0// 已经使用的宽度
        var height = 0 // 行的高度

        /**
         *
         * @param top Int
         * @param left Int
         */
        fun layout(top: Int, left: Int) {
            // 给view布局
            var mleft = left
            for (i in mViews.indices) {
                val view = mViews[i]
                var viewWidth = view.measuredWidth
                var viewHeight = view.measuredHeight
                view.measure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY))
                viewWidth = view.measuredWidth
                viewHeight = view.measuredHeight
                val avgHeight = ((height - viewHeight) / 2f + 0.5f).toInt()
                val vLeft = mleft
                val vTop = top + avgHeight
                val vRight = vLeft + viewWidth
                val vBottom = vTop + viewHeight
                view.layout(vLeft, vTop, vRight, vBottom)
                mleft += space + viewWidth
            }
        }

        /**
         * 添加view的方法
         *
         * @param view
         */
        fun addView(view: View) {
            val viewWidth = view.measuredWidth
            val viewHeight = view.measuredHeight
            if (mViews.size == 0) {
                if (viewWidth > maxWidth) {
                    usedWidth = maxWidth
                    height = viewHeight
                } else {
                    usedWidth = viewWidth
                    height = viewHeight
                }
            } else {
                usedWidth += space + viewWidth
                height = if (height > viewHeight) height else viewHeight // 选择大的高度
            }
            mViews.add(view)
        }

        /**
         * 判断是否可以往行中添加控件
         *
         * @param view
         */
        fun canAdd(view: View): Boolean {
            if (mViews.size == 0) {
                return true
            }
            // 已经使用的 + 间距+ 要加的view的宽度 > maxWidth ,添加不进来
//            return usedWidth + space + view.measuredWidth <= maxWidth

            return usedWidth+ view.measuredWidth <= maxWidth
        }
    }


    fun clearAllView() {
        mLines = ArrayList()
        this.invalidate()
    }
}