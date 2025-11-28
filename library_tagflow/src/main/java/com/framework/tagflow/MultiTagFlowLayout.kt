package com.framework.tagflow

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.util.isNotEmpty
import androidx.core.util.size
import androidx.core.view.isGone
import androidx.core.view.isNotEmpty
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.framework.tagflow.adapter.BaseTagAdapter
import com.framework.tagflow.databinding.TagFlowLayoutBinding
import com.framework.tagflow.interfac.OnTagClickListener
import com.framework.tagflow.interfac.OnTagSelectedListener
import com.framework.tagflow.tags.MutSelectedTagView
import com.framework.tagflow.tags.NonTouchableRecyclerView
import com.framework.tagflow.utils.DensityUtils
import com.framework.tagflow.view.FlowLayout
import kotlin.math.ceil

/**
 * @author: xiaxueyi
 * @date: 2022-09-27
 * @time: 09:54
 * @说明:多样 可折叠和展开的标签控件 流式布局，(RecyclerView和FlowLayout)
 */

open class MultiTagFlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        //默认标签之间的间距
        private const val DEFAULT_TAGS_SPACE = 8f
        //默认展开动画执行时间(毫秒)
        private const val DEFAULT_ANIMATION_DURATION = 400
        const val ITEM_MODEL_CLICK = 100000
        const val ITEM_MODEL_SELECT = 100001
        private const val LAYOUT_TYPE_DEFAULT = 0
        private const val LAYOUT_MANAGER_MODE_DEFAULT = 0
        private const val DEFAULT_ROWS = 3
        private const val MAX_FLOW_LAYOUT_LINES = 6
    }


    //是否折叠起来
    //true:折叠起来了
    //false:展开了
    private var isFolded = false

    private var foldHint = resources.getString(R.string.foldHint)   //展开后显示的提示文字

    private var expandHint =resources.getString(R.string.expandHint)   //折叠起来后显示的提示文字

    private var animationDuration = 0    //展开和折叠动画持续时间

    private var mHasMore = false//是否有展开的布局，查看更多

    private var mRecyclerViewAdapterDataSetObserver: RecyclerViewAdapterDataSetObserver? = null

    private var mRecyclerViewAdapter: RecyclerView.Adapter<*>? = null

    private var defaultRows:Int=3 //默认行数三行

    private var defaultColumn:Int=3;//默认列数三列

    private var isNotifyData:Boolean=false //是否刷新了数据

    @IntDef(ITEM_MODEL_CLICK, ITEM_MODEL_SELECT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ItemMode

    //标签模式，点击模式（默认），选中模式
    @ItemMode
    private var itemModel = 0

    private var mOnTagClickListener: OnTagClickListener? = null

    private var mSelectedListener: OnTagSelectedListener? = null

    private var mLayoutType:Int=0 //布局类型 0：RecyclerView，1：FlowLayout

    private var mAdapterDataSetObserver: AdapterDataSetObserver? = null //FlowLayout 适配器监听

    private var mBaseTagAdapter: BaseTagAdapter<*>? = null //FlowLayout 适配器
    
    private val mRecyclerView: NonTouchableRecyclerView by lazy { NonTouchableRecyclerView(context) }

    private val mBinding = TagFlowLayoutBinding.inflate(LayoutInflater.from(context), this)

    private val mFlowLayout: FlowLayout by lazy {
        FlowLayout(context).apply {
            setSpace(tagsHorizontalSpace, tagsVerticalSpace)
        }
    }

    private var tagsHorizontalSpace = 0     //标签之间的横向间距

    private var tagsVerticalSpace = 0    //标签之间的纵向间距

    private var mLayoutManagerMode:Int=0

    private val mEmptyView:View by lazy{LayoutInflater.from(context).inflate(R.layout.layout_empty_not_data,null)}

    /**
     * 用于保存需要设置点击事件的 tag item
     */
    private var mOnTagChildClickArray: SparseArray<OnTagChildClickListener<BaseTagAdapter<*>>>? = null

    private val defaultTagsSpacePx = DensityUtils.dp2px(context, DEFAULT_TAGS_SPACE)

    /**
     * 列表布局只支持垂直列表模式
     * @property index Int
     * @constructor
     */
    enum class LayoutManagerMode(val index: Int){
        LinearLayoutManager(0),
        GridLayoutManager(1),
    }


    /**
     * 布局类型
     * @property index Int
     * @constructor
     */
    enum class LayoutTypeMode(val index: Int){
        RecyclerView(0),
        FlowLayout(1),
    }



    /**
     * Linear layout manager
     */
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    /**
     * Grid layout manager
     */
    private val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(context, 3).apply {
            orientation = GridLayoutManager.VERTICAL
        }
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.MultiTagFlowLayout) {
            tagsHorizontalSpace = getDimension(
                R.styleable.MultiTagFlowLayout_tagsHorizontalSpace,
                defaultTagsSpacePx.toFloat()
            ).toInt()

            tagsVerticalSpace = getDimension(
                R.styleable.MultiTagFlowLayout_tagsVerticalSpace,
                defaultTagsSpacePx.toFloat()
            ).toInt()

            animationDuration =
                getInt(R.styleable.MultiTagFlowLayout_animationDuration, DEFAULT_ANIMATION_DURATION)

            mHasMore = getBoolean(R.styleable.MultiTagFlowLayout_hasMore, false)

            mLayoutType = getInt(R.styleable.MultiTagFlowLayout_layout_type, LAYOUT_TYPE_DEFAULT)

            mLayoutManagerMode = getInt(R.styleable.MultiTagFlowLayout_layoutManager_Mode, LAYOUT_MANAGER_MODE_DEFAULT)

            foldHint = formatString(
                getString(R.styleable.MultiTagFlowLayout_foldHint),
                foldHint
            )

            expandHint = formatString(
                getString(R.styleable.MultiTagFlowLayout_expandHint),
                expandHint
            )

            itemModel = ITEM_MODEL_CLICK

        }

        /**
         * 实例化组件
         */
        initWidget()


        /**
         * 实例化组件类型
         */
        initLayoutType()


        /**
         * 初始化列表的布局管理模式
         */
        initLayoutManage()

    }


    /**
     * 初始化组件
     */
    private fun initWidget() {
        mBinding.rlShowMore.setOnClickListener {
            //真实数量
            var realCount =0
            var finalLine = 0
            when (mLayoutType) {
                LayoutTypeMode.RecyclerView.index -> {
                    val realCount = mRecyclerViewAdapter?.itemCount ?: 0
                    val maxLine = when (val layoutManager = mRecyclerView.layoutManager) {
                        is GridLayoutManager -> {
                            if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                                realCount.toDouble() / defaultColumn.toDouble() // 最大行数
                            } else {
                                realCount.toDouble()
                            }
                        }
                        else -> realCount.toDouble()
                    }
                    finalLine = ceil(maxLine).toInt() // 最终行数，向上取整
                }

                LayoutTypeMode.FlowLayout.index -> {
                    realCount = minOf(mFlowLayout.getLineSize(), MAX_FLOW_LAYOUT_LINES)
                }
            }

            // 展开/收起动画
            toggleExpand(finalLine, realCount)
        }

    }

    /**
     *动画
     * @param animateStart Int
     * @param animateEnd Int
     */
    private fun animate(animateStart: Int, animateEnd: Int) {
        var start:Int = animateStart
        var end = animateEnd
        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{
                if (start < 0) {
                    start = mRecyclerView.measuredHeight
                }
                if (end < 0) {
                    end = mRecyclerView.measuredHeight
                }
            }

            LayoutTypeMode.FlowLayout.index->{
                if (start < 0) {
                    start = mFlowLayout.measuredHeight
                }
                if (end < 0) {
                    end = mFlowLayout.measuredHeight
                }
            }

            else->{
                start=0
                end=0
            }
        }

        val animator: ValueAnimator = ValueAnimator.ofInt(start, end)
        animator.duration = animationDuration.toLong()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            mBinding.hsvTagContent.updateLayoutParams {
                this.height=value
            }
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }



    /**
     * Toggle expand
     * 展开/收起动画的通用逻辑
     * @param finalLine 最终行数
     * @param realCount 真实数量
     */
    private fun toggleExpand(finalLine: Int, realCount: Int) {
        val animateStart: Int
        val animateEnd: Int
        val isRecyclerView = mLayoutType == LayoutTypeMode.RecyclerView.index
        if (!isFolded) { // 展开
            animateStart = if (isRecyclerView) {
                getLineHeight() * defaultRows
            } else {
                getLineHeight() * 3
            }
            animateEnd = if (isRecyclerView) {
                getLineHeight() * finalLine
            } else {
                getLineHeight() * realCount
            }
        } else { // 收起
            animateStart = if (isRecyclerView) {
                getLineHeight() * finalLine
            } else {
                getLineHeight() * realCount
            }
            animateEnd = if (isRecyclerView) {
                getLineHeight() * defaultRows
            } else {
                getLineHeight() * 3
            }
        }

        val rotationStart = if (isFolded) {
            -180f
        } else {
            0f
        }
        val rotationEnd = if (isFolded) {
            0f
        } else {
            180f
        }
        ObjectAnimator.ofFloat(mBinding.ivArrowMore, "rotation", rotationStart, rotationEnd).start()
        animate(animateStart, animateEnd)
        mBinding.tvMoreHint.text = if (isFolded) {
            expandHint
        } else {
            foldHint
        }
        isFolded = !isFolded
    }

    /**
     * 重新设置数据
     */
    private fun reloadData() {
        val itemCount = when (mLayoutType) {
            LayoutTypeMode.RecyclerView.index -> {
                mRecyclerViewAdapter?.itemCount ?: 0
            }
            LayoutTypeMode.FlowLayout.index -> {
                mBaseTagAdapter?.count ?: 0
            }
            else -> 0
        }

        /**
         * 如果行数为0，重新渲染布局
         */
        if (itemCount == 0) {
            updateLayoutForEmptyData()
            return
        }
        


        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{ //RecyclerView
                mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                    override fun onGlobalLayout() {
                        mBinding.hsvTagContent.updateLayoutParams {
                            this.height = calculateControlScrollViewHeight(itemCount) //计算 ControlScrollView 的高度
                        }
                        mRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }

            LayoutTypeMode.FlowLayout.index->{ //FlowLayout

                /**
                 * 绑定点击事件
                 */
                bindViewClickListener()

                mFlowLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                    override fun onGlobalLayout() {
                        val flowLayoutLines = mFlowLayout.getLineSize()
                        val controlScrollViewHeight = if (mHasMore) { // 有展开更多的布局
                            if (mBinding.rlShowMore.isGone){
                                mBinding.rlShowMore.visibility = VISIBLE
                                mBinding.rlShowMore.isClickable = true
                            }
                            if (flowLayoutLines <= DEFAULT_ROWS) {
                                getLineHeight()  * flowLayoutLines
                            } else {
                                getLineHeight() * DEFAULT_ROWS
                            }
                        } else { // 没有展开更多的布局
                            mBinding.rlShowMore.visibility = View.GONE
                            mBinding.rlShowMore.isClickable = false // 避免点击事件
                            getLineHeight() * flowLayoutLines
                        }

                        mBinding.hsvTagContent.updateLayoutParams {
                            this.height=controlScrollViewHeight
                        }
                        mFlowLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }


    /**
     * 初始化列表的布局管理模式
     */
    private fun initLayoutManage(){
        if(mLayoutType== LayoutTypeMode.RecyclerView.index){
            when(mLayoutManagerMode){
                LayoutManagerMode.LinearLayoutManager.index->{
                    mRecyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = linearLayoutManager
                        (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
                        itemAnimator = null // 或者直接设置成 null
                    }
                }

                LayoutManagerMode.GridLayoutManager.index->{
                    mRecyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = gridLayoutManager
                        (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
                    }
                }
            }
        }
    }



    /**
     * RecyclerView监听
     */
    internal inner class RecyclerViewAdapterDataSetObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            reloadData()
        }
    }


    /**
     * Flowlayout监听
     */
    internal inner class AdapterDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            reloadData()
        }
    }



    /**
     * 实例化布局类型
     */
    private fun initLayoutType() {
        if (mBinding.hsvTagContent.isNotEmpty()) {
            mBinding.hsvTagContent.removeAllViews()
        }
        when (mLayoutType) {
            LayoutTypeMode.RecyclerView.index -> {
                mBinding.hsvTagContent.addView(mRecyclerView)
            }

            LayoutTypeMode.FlowLayout.index -> {
                mBinding.hsvTagContent.addView(mFlowLayout)
            }
        }
    }


    /**
     *
     * @return Int
     */
    private fun getLineHeight(): Int {
        var itemHeight = 0
        var spaceHeight:Int=0; //间隔间距
        var spacing: Int=0 //分割线高度，如果设置了分割线，计算item 的高度需要加上
        when (mLayoutType) {
            LayoutTypeMode.RecyclerView.index -> {
                val layoutManager = mRecyclerView.layoutManager ?: return 0
                val childView = mRecyclerView.getChildAt(0) ?: return 0

                // 安全获取分割线高度
                if (mRecyclerView.itemDecorationCount > 0) {
                    when (layoutManager) {
                        is GridLayoutManager -> {
                            val itemDecoration = mRecyclerView.getItemDecorationAt(0)
                            spacing = if (layoutManager.orientation == GridLayoutManager.VERTICAL && itemDecoration is GridLayoutItemDecoration) {
                                itemDecoration.getSpaceHeight()
                            }else{
                                0
                            }
                        }

                        is LinearLayoutManager -> {
                            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                                spacing = 1
                            }
                        }
                    }
                }

                //子view 的高度
                itemHeight= if (childView.height > 0) childView.height else childView.measuredHeight
                //获取设置子view 内外间距的尺寸
                spaceHeight=childView.marginTop+childView.marginBottom
                //计算整个item 的高度
                itemHeight=(itemHeight+spaceHeight+spacing)
            }

            LayoutTypeMode.FlowLayout.index -> {
                itemHeight = mFlowLayout.getLineHeight() + tagsVerticalSpace
            }

            else -> itemHeight = 0
        }

        return itemHeight
    }



    /**
     *
     * @param adapter Any
     */
    fun setAdapter(adapter:Any){
        when (adapter) {
            is RecyclerView.Adapter<*> -> {
                // 取消注册旧的观察者
                mRecyclerViewAdapterDataSetObserver?.let { mRecyclerViewAdapter?.unregisterAdapterDataObserver(it) }
                this.mRecyclerViewAdapter = adapter
                mRecyclerView.adapter = mRecyclerViewAdapter
                mRecyclerViewAdapter?.let { recyclerViewAdapter ->
                    mRecyclerViewAdapterDataSetObserver = RecyclerViewAdapterDataSetObserver()
                    recyclerViewAdapter.registerAdapterDataObserver(mRecyclerViewAdapterDataSetObserver!!)
                    reloadData()
                }
            }
            is BaseTagAdapter<*> -> {
                // 取消注册旧的观察者
                mAdapterDataSetObserver?.let { mBaseTagAdapter?.unregisterDataSetObserver(it) }
                this.mBaseTagAdapter = adapter
                mBaseTagAdapter?.let { tagAdapterIt ->
                    mAdapterDataSetObserver = AdapterDataSetObserver()
                    tagAdapterIt.registerDataSetObserver(mAdapterDataSetObserver)
                    reloadData()
                }
            }
            else -> {
                // 处理不支持的 Adapter 类型
                Log.w("setAdapter", "adapter type: ${adapter.javaClass.name}")
            }
        }
    }


    /**
     * 回收对象数据，防止oom
     * Gc data
     *
     */
    private fun gcData(){
        if (mOnTagChildClickArray?.isNotEmpty() == true){
            mOnTagChildClickArray?.clear()
        }

        mRecyclerViewAdapterDataSetObserver?.let {
            mRecyclerViewAdapter?.unregisterAdapterDataObserver(it)
        }

        mBaseTagAdapter?.let {
            if (mAdapterDataSetObserver != null) {
                it.unregisterDataSetObserver(mAdapterDataSetObserver)
            }
            it.getData().toMutableList().clear()
        }

        linearLayoutManager.removeAllViews()
        gridLayoutManager.removeAllViews()


        if (mFlowLayout.isNotEmpty()) {
            mFlowLayout.clearAllView()
        }

        if (mRecyclerView.isNotEmpty()) {
            mRecyclerView.removeAllViews()
        }

        if (this.isNotEmpty()){
            this.removeAllViews()
        }
    }


    /**
     * 获取适配器
     */
    fun getAdapter():Any? {
        return when (mLayoutType) {
            LayoutTypeMode.RecyclerView.index -> {
                mRecyclerViewAdapter
            }
            LayoutTypeMode.FlowLayout.index->{
                mBaseTagAdapter
            }
            else -> {
                null
            }
        }
    }


    /**
     * 刷新数据
     */
    fun notifyDataSetChanged(){
        when (mLayoutType) {
            LayoutTypeMode.RecyclerView.index -> {
                mRecyclerViewAdapter?.let { adapter ->
                    // 当itemCount大于0时才刷新
                    if (adapter.itemCount > 0) {
                        adapter.notifyItemRangeChanged(0, adapter.itemCount)
                    }
                }
            }

            LayoutTypeMode.FlowLayout.index -> {
                mBaseTagAdapter?.notifyDataSetChanged()
            }
        }
        //标识：刷新数据  isNotifyData=true
        isNotifyData=true
    }

    /**
     * 绑定 item tag 点击事件
     * Bind view click listener
     */
    protected open fun bindViewClickListener(){
        val isFlowLayoutEmpty = mFlowLayout.isNotEmpty()
        if (isFlowLayoutEmpty) {
            mFlowLayout.removeAllViews()
        }
        mBaseTagAdapter?.let { tagAdapterIt->
            for (i in 0 until tagAdapterIt.count) {
                val itemView = tagAdapterIt.getView(i, convertView = null, mFlowLayout)
                val position:Int=i
                if (itemModel == ITEM_MODEL_CLICK && mOnTagClickListener != null) {
                    itemView.setOnClickListener { v ->
                        mOnTagClickListener?.onClick(v,position)
                    }

                    itemView.setOnLongClickListener { v ->
                        mOnTagClickListener?.onLongClick(v, position)
                        true
                    }

                }else if (itemModel == ITEM_MODEL_SELECT && mSelectedListener != null) {

                    itemView.setOnClickListener { v ->
                        if (v is MutSelectedTagView) {
                            if (!v.isSelected) {
                                tagAdapterIt.select(position)
                                mSelectedListener?.selected(v, position, tagAdapterIt.getData())
                            } else {
                                tagAdapterIt.unSelect(position)
                                mSelectedListener?.unSelected(v, position, tagAdapterIt.getData())
                            }
                            v.toggle()
                        }
                    }
                }

                /**
                 * tag 子view 点击事件
                 */
                mOnTagChildClickArray?.let {

                    for (i in 0 until it.size) {
                        val id = it.keyAt(i)
                        itemView.findViewById<View>(id)?.let { childView ->
                            childView.setOnClickListener { v ->
                                onItemChildClick(v, position)
                            }
                        }
                    }
                }
                mFlowLayout.addView(itemView)
            }
        }
    }


    /**
     * 更新空布局宽高
     * Update layout for empty data
     *
     */
    private fun updateLayoutForEmptyData() {
        mBinding.hsvTagContent.updateLayoutParams {
            this.height = 0
        }
        val childCount:Int= mBinding.flEmpty.childCount
        if(childCount>0){
            mBinding.flEmpty.removeAllViews()
        }
        mBinding.flEmpty.addView(mEmptyView)
        mBinding.flEmpty.visibility= VISIBLE
        mBinding.rlShowMore.visibility = INVISIBLE
        mBinding.rlShowMore.isClickable = false
    }



    /**
     * 计算行高
     * Calculate control scroll view height
     *
     * @param itemCount
     */
    private fun calculateControlScrollViewHeight(itemCount: Int): Int {
        val maxLine = if (mRecyclerView.layoutManager is GridLayoutManager) {
            val gridLayoutManager = mRecyclerView.layoutManager as GridLayoutManager
            if (gridLayoutManager.orientation == GridLayoutManager.VERTICAL) {
                itemCount.toDouble() / defaultColumn.toDouble()
            } else {
                itemCount.toDouble()
            }
        } else {
            itemCount.toDouble()
        }
        val finalLine = ceil(maxLine).toInt()
        val controlScrollViewHeight= if (mHasMore) { // 有展开更多的布局
            if (isNotifyData) {
                if (finalLine <= 3) {
                    mBinding.rlShowMore.visibility = GONE
                    getLineHeight() * finalLine
                } else {
                    mBinding.rlShowMore.visibility = VISIBLE
                    if (isFolded) {
                        getLineHeight() * finalLine
                    } else {
                        getLineHeight() * defaultRows
                    }
                }
            } else {
                if (finalLine <= 3) {
                    mBinding.rlShowMore.visibility = GONE
                    getLineHeight() * finalLine
                } else {
                    mBinding.rlShowMore.visibility = VISIBLE
                    getLineHeight() * defaultRows
                }
            }
        } else { // 没有展开更多的布局
            mBinding.rlShowMore.visibility = GONE
            getLineHeight() * itemCount // 使用之前获取的 itemCount
        }

        return controlScrollViewHeight
    }



    /**
     * 使用 get() 函数获取 RecyclerView 实例
     * Recycler view
     */
    val recyclerView: NonTouchableRecyclerView
        get() = mRecyclerView


    /**
     * 使用 get() 函数获取 FlowLayout 实例
     * Flow layout
     */
    val flowLayout: FlowLayout
        get() = mFlowLayout


    /**
     * 获取展开后显示的提示文字
     * @return String
     */
    fun getFoldHint(): String {
        return foldHint
    }


    /**
     * 设置展开后显示的提示文字
     * @param foldHint String
     */
    fun setFoldHint(foldHint: String) {
        this.foldHint = foldHint
        if (isFolded) {
            mBinding.tvMoreHint.text = foldHint
        }
    }


    /**
     * 获取展开文字
     * @return String
     */
    fun getExpandHint(): String {
        return expandHint
    }


    /**
     * 设置展开后的文字
     * @param expandHint String
     */
    fun setExpandHint(expandHint: String) {
        this.expandHint = expandHint
        if (!isFolded) {
            mBinding.tvMoreHint.text = expandHint
        }
    }


    /**
     * 获取动画时间
     * @return Int
     */
    fun getAnimationDuration(): Int {
        return animationDuration
    }

    /**
     * 设置动画时间
     * @param animationDuration Int
     */
    fun setAnimationDuration(animationDuration: Int) {
        this.animationDuration = animationDuration
    }


    /**
     * 获取FlowLayout 点击模式
     * @return Int
     */
    fun getItemModel(): Int {
        return itemModel
    }

    /**
     * 设置FlowLayout 点击模式，作为点击的标识
     * @param itemModel Int
     */
    fun setItemModel(@ItemMode itemModel: Int) {
        this.itemModel = itemModel
        reloadData()
    }

    /**
     * 获取水平间距
     * @return Int
     */
    fun getTagsHorizontalSpace(): Int {
        return tagsHorizontalSpace
    }

    /**
     * 设置水平间距
     * @param tagsHorizontalSpace Int
     */
    fun setTagsHorizontalSpace(tagsHorizontalSpace: Int) {
        this.tagsHorizontalSpace = tagsHorizontalSpace
    }

    /**
     * 获取垂直间距
     * @return Int
     */
    fun getTagsVerticalSpace(): Int {
        return tagsVerticalSpace
    }

    /**
     * 设置垂直间距
     * @param tagsVerticalSpace Int
     */
    fun setTagsVerticalSpace(tagsVerticalSpace: Int) {
        this.tagsVerticalSpace = tagsVerticalSpace
    }

    /**
     * 格式化字符
     * @param str
     * @param defaultString
     * @return
     */
    private fun formatString(str: String?, defaultString: String): String {
        return if (str.isNullOrEmpty()) {
            defaultString
        } else {
            str
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gcData()
    }

    /******************************************************* set listener *********************************************************************/


    fun setOnTagClickListener(onTagClickListener: OnTagClickListener)=apply {
        this.mOnTagClickListener=onTagClickListener
        if (mBaseTagAdapter != null) {
            reloadData()
        }
    }


    open fun setSelectedListener(selectedListener: OnTagSelectedListener?) {
        this.mSelectedListener = selectedListener
        if (mBaseTagAdapter != null) {
            reloadData()
        }
    }


    /**
     * 增加子 tag view 点击事件
     * Add on tag child click listener
     * @param id
     * @param listener
     */
    fun addOnTagChildClickListener(@IdRes id: Int, listener: OnTagChildClickListener<BaseTagAdapter<*>>) = apply {
        mOnTagChildClickArray =
            (mOnTagChildClickArray?: SparseArray<OnTagChildClickListener<BaseTagAdapter<*>>>()).apply {
                put(id, listener)
            }
    }

    /**
     *
     * On item child click
     * @param v
     * @param position
     */
    protected open fun onItemChildClick(v: View, position: Int) {
        mOnTagChildClickArray?.get(v.id)?.onItemClick(mBaseTagAdapter!!, v, position)
    }

    /**
     * On tag child click listener
     * @param T
     * @constructor Create empty On tag child click listener
     */

    fun interface OnTagChildClickListener<T : Any> {
        fun onItemClick(adapter: T, view: View, position: Int)
    }
}