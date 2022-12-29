package com.framework.tagflow

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.annotation.IntDef
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.framework.tagflow.adapter.BaseTagAdapter
import com.framework.tagflow.bean.BaseTagBean
import com.framework.tagflow.interfac.OnTagClickListener
import com.framework.tagflow.interfac.OnTagSelectedListener
import com.framework.tagflow.tags.MutSelectedTagView
import com.framework.tagflow.utils.DensityUtils
import com.framework.tagflow.view.ControlScrollView
import com.framework.tagflow.view.FlowLayout
import kotlin.math.ceil

/**
 * @author: xiaxueyi
 * @date: 2022-09-27
 * @time: 09:54
 * @说明:多样 可折叠和展开的标签控件 流式布局，(RecyclerView和FlowLayout)
 */

open class MultiTagFlowLayout @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(mContext, attrs, defStyleAttr) {

    private lateinit var mControlScrollView: ControlScrollView     //标签容器

    private lateinit var mIvArrowMore: ImageView    //箭头

    private lateinit var mTvMoreHint: TextView    //提示文本

    private lateinit var mRlShowMore: LinearLayout    //展开收起布局

    private lateinit var mEmptyViewContainer:FrameLayout;

    //是否折叠起来
    //true:折叠起来了
    //false:展开了
    private var isFolded = false

    private var foldHint = ""    //展开后显示的提示文字

    private var expandHint = ""   //折叠起来后显示的提示文字

    private var animationDuration = 0    //展开和折叠动画持续时间

    private var mHasMore = false//是否有展开的布局，查看更多

    private var mRecyclerViewAdapterDataSetObserver: RecyclerViewAdapterDataSetObserver? = null

    private var mRecyclerViewAdapter: RecyclerView.Adapter<*>? = null

    private lateinit var mRecyclerView: RecyclerView

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

    private var mTagAdapter: BaseTagAdapter<*>? = null //FlowLayout 适配器

    private lateinit var mFlowLayout: FlowLayout

    private var tagsHorizontalSpace = 0     //标签之间的横向间距

    private var tagsVerticalSpace = 0    //标签之间的纵向间距

    private var mLayoutManagerMode:Int=0

    private lateinit var mEmptyView:View;


    companion object {

        //默认标签之间的间距
        private const val DEFAULT_TAGS_SPACE = 8f

        //默认展开动画执行时间(毫秒)
        private const val DEFAULT_ANIMATION_DURATION = 400

        const val ITEM_MODEL_CLICK = 100000

        const val ITEM_MODEL_SELECT = 100001

    }


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

    init {
        val typedArray: TypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout)

        tagsHorizontalSpace = typedArray.getDimension(R.styleable.TagFlowLayout_tagsHorizontalSpace, DensityUtils.dp2px(mContext, DEFAULT_TAGS_SPACE).toFloat()).toInt()

        tagsVerticalSpace = typedArray.getDimension(R.styleable.TagFlowLayout_tagsVerticalSpace, DensityUtils.dp2px(mContext, DEFAULT_TAGS_SPACE).toFloat()).toInt()

        animationDuration = typedArray.getInt(R.styleable.TagFlowLayout_animationDuration, DEFAULT_ANIMATION_DURATION)

        mHasMore = typedArray.getBoolean(R.styleable.TagFlowLayout_hasMore, false)

        mLayoutType=typedArray.getInt(R.styleable.TagFlowLayout_layout_type,0)

        mLayoutManagerMode=typedArray.getInt(R.styleable.TagFlowLayout_layoutManager_Mode,0)

        foldHint = formatString(typedArray.getString(R.styleable.TagFlowLayout_foldHint),resources.getString(R.string.foldHint))

        expandHint = formatString(typedArray.getString(R.styleable.TagFlowLayout_expandHint),resources.getString(R.string.expandHint))

        itemModel = ITEM_MODEL_CLICK

        typedArray.recycle()

        /**
         * 实例化组件
         */
        initWidget()


        /**
         * 实例化组件类型
         */
        initLayoutType();


        /**
         *
         */
        initLayoutManage()

    }


    /**
     * 初始化组件
     */
    private fun initWidget() {

        View.inflate(mContext, R.layout.tag_flow_layout, this)
        mControlScrollView = findViewById(R.id.hsv_tag_content)
        mIvArrowMore = findViewById(R.id.iv_arrow_more)
        mTvMoreHint = findViewById(R.id.tv_more_hint)
        mRlShowMore = findViewById(R.id.rl_show_more)
        mEmptyViewContainer= findViewById(R.id.fl_empty)
        mEmptyView=LayoutInflater.from(context).inflate(R.layout.layout_empty_not_data,this,false)
        mControlScrollView.isFillViewport=true
        this.isFillViewport=true
        mRlShowMore.setOnClickListener(View.OnClickListener {

            var maxLine:Double =0.0//最大行数

            //真实数量
            var realCount=0;

            var finalLine:Int=0

            var animateStart: Int=0 //记录动画开始位置

            var animateEnd: Int=0 //记录动画结束位置

            when(mLayoutType){
                LayoutTypeMode.RecyclerView.index->{
                    realCount= mRecyclerViewAdapter?.itemCount?:0 ;

                    if(mRecyclerView.layoutManager is GridLayoutManager){
                        val gridLayoutManager:GridLayoutManager= mRecyclerView.layoutManager as GridLayoutManager
                        if(gridLayoutManager.orientation==GridLayoutManager.VERTICAL){
                            //计算列表中有多少行
                            maxLine = (realCount.toDouble()/defaultColumn.toDouble()) //最大行数
                        }
                    }else{
                        maxLine=realCount.toDouble();
                    }
                    //最终行数，向上取整,
                    finalLine= ceil(maxLine).toInt()
                }

                LayoutTypeMode.FlowLayout.index->{
                    realCount=mFlowLayout.getLineSize()

                    if (realCount >= 6){
                        realCount = 6
                    }

                } else->{
                    realCount=0
                }
            }

            //展开容器
            if (!isFolded) {
                ObjectAnimator.ofFloat(mIvArrowMore, "rotation", 0f, 180f).start()

                if(mLayoutType== LayoutTypeMode.RecyclerView.index){
                    animateStart=getLineHeight() * defaultRows
                    animateEnd=getLineHeight() * finalLine
                }else{
                    animateStart=getLineHeight() * 3
                    animateEnd=getLineHeight() * realCount
                }


                animate(animateStart,animateEnd )

                mTvMoreHint.text = foldHint //点击收回
                mControlScrollView.setCanScroll(false)  //设置不可滑动

            } else {
                ObjectAnimator.ofFloat(mIvArrowMore, "rotation", -180f, 0f).start()

                if(mLayoutType== LayoutTypeMode.RecyclerView.index){
                    animateStart=getLineHeight() * finalLine
                    animateEnd=getLineHeight() * defaultRows
                }else{
                    animateStart=getLineHeight() * realCount
                    animateEnd=getLineHeight() * 3
                }

                animate(animateStart, animateEnd)

                mTvMoreHint.text = expandHint //点击展开
                mControlScrollView.setCanScroll(false) //设置不可滑动
            }
            isFolded = !isFolded
        })

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

            }else->{

            }
        }

        val animator: ValueAnimator = ValueAnimator.ofInt(start, end)

        animator.duration = animationDuration.toLong()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = mControlScrollView.layoutParams
            layoutParams.height = value
            mControlScrollView.layoutParams = layoutParams
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }


    /**
     * 重新设置数据
     */
    private fun reloadData() {
        var lines:Int=0;
        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{ //RecyclerView
                mRecyclerView.removeAllViews()
                lines= mRecyclerViewAdapter?.itemCount ?: 0
            }

            LayoutTypeMode.FlowLayout.index->{ //FlowLayout
                mFlowLayout.removeAllViews()
                lines= mTagAdapter?.count ?: 0
            }
            else->{
                lines=0;
            }
        }
        /**
         * 如果行数为0，重新渲染布局
         */
        if (lines == 0) {
            val params: ViewGroup.LayoutParams=this.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            this.layoutParams=params

            val layoutParams: ViewGroup.LayoutParams = mControlScrollView.layoutParams
            layoutParams.height = 0
            mControlScrollView.layoutParams = layoutParams
            /**
             * 增加空布局
             */
            mEmptyViewContainer.removeAllViews()
            mEmptyViewContainer.addView(mEmptyView)
            mEmptyViewContainer.visibility = View.VISIBLE
            mRlShowMore.visibility = View.GONE
            return
        }
        val childCount:Int= mEmptyViewContainer.childCount
        if(childCount>0){
            mEmptyViewContainer.removeAllViews()
            mEmptyViewContainer.visibility= GONE
        }

        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{ //RecyclerView

//                if(getAdapter() is BaseQuickAdapter<*,*>){
//                    (getAdapter() as BaseQuickAdapter<*,*>).setOnItemClickListener { adapter, view, position ->
//                        mOnItemClickListener?.onItemClick(position)
//                    }
//                }


                mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                    override fun onGlobalLayout() {
                        val itemCount:Int= mRecyclerViewAdapter?.itemCount?:0

                        var realCount:Int= mRecyclerViewAdapter?.itemCount?:0 ;
                        var maxLine =0.0//最大行数

                        if(mRecyclerView.layoutManager is GridLayoutManager){
                            val gridLayoutManager:GridLayoutManager= mRecyclerView.layoutManager as GridLayoutManager
                            if(gridLayoutManager.orientation==GridLayoutManager.VERTICAL){
                                //计算列表中有多少行
                                maxLine = (realCount.toDouble()/defaultColumn.toDouble()) //最大行数
                            }
                        }else{
                            maxLine=realCount.toDouble();
                        }
                        //最终行数，向上取整,
                        val finalLine= ceil(maxLine).toInt()


                        if (mHasMore) { //有展开更多的布局
                            val layoutParams: ViewGroup.LayoutParams = mControlScrollView.layoutParams
                            /**
                            1.如果已經在爆開的情況下
                            update Data 的話就不用再次收起來
                            2.但如果是在收起的情況下
                            update Data 就保持收起
                            3. 如果在爆開的情況下 按"取消"返回上一頁
                              下一次進來要收起
                            首次进入也是收起
                             true 标识已经刷新数据了，特殊判断
                            */
                            if(isNotifyData){
                                if (finalLine <= 3) {
                                    layoutParams.height = getLineHeight() * finalLine
                                    mRlShowMore.visibility = View.GONE
                                }else{
                                    mRlShowMore.visibility = View.VISIBLE
                                    //表示已经展开了
                                    if (isFolded){
                                        layoutParams.height = getLineHeight() * finalLine
                                    }else{
                                        //设置默认展开高度(一个item 高度*多少行)
                                        layoutParams.height = getLineHeight()* defaultRows
                                    }
                                }
                                mControlScrollView.layoutParams = layoutParams
                            }else{
                                if (finalLine <= 3) {
                                    layoutParams.height = getLineHeight() * finalLine
                                    mRlShowMore.visibility = View.GONE
                                } else {
                                    //设置默认展开高度(一个item 高度*多少行)
                                    layoutParams.height = getLineHeight()* defaultRows
                                    mRlShowMore.visibility = View.VISIBLE
                                }
                                mControlScrollView.layoutParams = layoutParams
                            }


                        } else { //没有展开更多的布局
                            mRlShowMore.visibility = View.GONE
                            val layoutParams: ViewGroup.LayoutParams? = mControlScrollView.layoutParams
                            if (layoutParams != null) {
                                layoutParams.height = getLineHeight() * itemCount
                            }
                            mControlScrollView.layoutParams = layoutParams
                        }
                        mRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }

            LayoutTypeMode.FlowLayout.index->{ //FlowLayout
                mFlowLayout.clearAllView()
                mFlowLayout.removeAllViews()
                for (i in 0 until mTagAdapter!!.count) {
                    val view = mTagAdapter?.getView(i, convertView = null, mFlowLayout)

                    var position:Int=i;
                    if (itemModel == ITEM_MODEL_CLICK && mOnTagClickListener != null) {

                        view?.setOnClickListener { v ->
                            mOnTagClickListener?.onClick(v,position)
                        }

                        view?.setOnLongClickListener(object : OnLongClickListener {
                            override fun onLongClick(v: View): Boolean {
                                mOnTagClickListener?.onLongClick(v, position)
                                return true
                            }
                        })
                    } else if (itemModel ==ITEM_MODEL_SELECT && mSelectedListener != null) {
                        view?.setOnClickListener { v ->
                            if (v is MutSelectedTagView) {
                                if (!v.isSelected) {
                                    mTagAdapter!!.select(position)
                                    mSelectedListener!!.selected(v, position, mTagAdapter!!.getData() as List<BaseTagBean?>)
                                } else {
                                    mTagAdapter!!.unSelect(position)
                                    mSelectedListener!!.unSelected(v, position, mTagAdapter!!.getData() as List<BaseTagBean?>)
                                }
                                v.toggle()
                            }
                        }
                    }

                    mFlowLayout.addView(view)
                }

                mFlowLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                    override fun onGlobalLayout() {
                        if (mHasMore) { //有展开更多的布局
                            val lines: Int = mFlowLayout.getLineSize()
                            val layoutParams: ViewGroup.LayoutParams = mControlScrollView.layoutParams
                            if (lines <= 3) {
                                layoutParams.height = getLineHeight() * lines
                                mRlShowMore.visibility = View.GONE
                            } else {
                                layoutParams.height = getLineHeight() * 3
                                mRlShowMore.visibility = View.VISIBLE
                            }
                            mControlScrollView.layoutParams = layoutParams

                        } else { //没有展开更多的布局
                            mRlShowMore.visibility = View.GONE
                            var layoutParams: ViewGroup.LayoutParams? = mControlScrollView.layoutParams
                            val lines: Int = mFlowLayout.getLineSize()
                            if (layoutParams != null) {
                                layoutParams.height = getLineHeight() * lines
                            }
                            mControlScrollView.layoutParams = layoutParams
                        }
                        mFlowLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }

            else->{

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
                    val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation= LinearLayoutManager.VERTICAL
                    mRecyclerView.setHasFixedSize(true)
                    mRecyclerView.layoutManager =linearLayoutManager;
                    mRecyclerView.itemAnimator = DefaultItemAnimator()
                }


                LayoutManagerMode.GridLayoutManager.index->{
                    val gridLayoutManager: GridLayoutManager = GridLayoutManager(context,3)
                    gridLayoutManager.orientation= GridLayoutManager.VERTICAL
                    mRecyclerView.setHasFixedSize(true)
                    mRecyclerView.layoutManager =gridLayoutManager;
                    mRecyclerView.itemAnimator = DefaultItemAnimator()
                }
                else->{
                    Toast.makeText(context,"un know",Toast.LENGTH_SHORT).show()
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

        override fun onInvalidated() {
            super.onInvalidated()
        }
    }



    /**
     * 实例化布局类型
     */
    private fun initLayoutType(){
        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{
                mControlScrollView.removeAllViews()
                mRecyclerView = RecyclerView(mContext)
                mControlScrollView.addView(mRecyclerView)
            }

            LayoutTypeMode.FlowLayout.index->{
                mControlScrollView.removeAllViews()
                mFlowLayout = FlowLayout(mContext)
                mControlScrollView.addView(mFlowLayout)
                mFlowLayout.setSpace(tagsHorizontalSpace, tagsVerticalSpace)
            }

            else->{
                Toast.makeText(mContext,"Un know ",Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     *
     * @return Int
     */
    private fun getLineHeight(): Int {
        var itemHeight:Int= 0 //每一个item 的高度
        var spaceHeight:Int=0; //间隔间距
        var spacing: Int=0 //分割线高度，如果设置了分割线，计算item 的高度需要加上

        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{
                val childCount:Int= mRecyclerView.childCount
                println(childCount)

                val childView=mRecyclerView.getChildAt(0)
                println(childView)

                val itemDecorationCount:Int=mRecyclerView.itemDecorationCount
                if(itemDecorationCount>0){
                    if(mRecyclerView.layoutManager is GridLayoutManager){
                        val itemDecoration:ItemDecoration=mRecyclerView.getItemDecorationAt(0)
                        val gridLayoutManager:GridLayoutManager= mRecyclerView.layoutManager as GridLayoutManager
                        if(gridLayoutManager.orientation==GridLayoutManager.VERTICAL){
                            if(itemDecoration is GridLayoutItemDecoration){
                                val gridLayoutItemDecoration: GridLayoutItemDecoration = itemDecoration as GridLayoutItemDecoration
                                spacing= gridLayoutItemDecoration.getSpaceHeight()
                            }else{
                                spacing=0
                            }
                        }
                    }
                }

                if(childView!=null){
                    //子view 的高度
                    itemHeight=childView.measuredHeight

                    //获取设置子view 内外间距的尺寸
                    spaceHeight=childView.marginTop+childView.marginBottom

                    //计算整个item 的高度
                    itemHeight=(itemHeight+spaceHeight+spacing)

                    println(itemHeight)
                }
            }

            LayoutTypeMode.FlowLayout.index->{
                itemHeight= mFlowLayout.getLineHeight() + tagsVerticalSpace
            }

            else->{
                itemHeight=0
            }
        }


        return itemHeight
    }

    /**
     *
     * @param adapter Any
     */
    fun setAdapter(adapter:Any){
        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{
                if(adapter is RecyclerView.Adapter<*>){
                    this.mRecyclerViewAdapter = adapter as RecyclerView.Adapter<*>

                    mRecyclerView.adapter=mRecyclerViewAdapter

                    if (mRecyclerViewAdapter != null && mRecyclerViewAdapterDataSetObserver != null) {
                        this.mRecyclerViewAdapter!!.unregisterAdapterDataObserver(mRecyclerViewAdapterDataSetObserver!!)
                    }
                    if (this.mRecyclerViewAdapter != null) {
                        mRecyclerViewAdapterDataSetObserver = RecyclerViewAdapterDataSetObserver()
                        this.mRecyclerViewAdapter!!.registerAdapterDataObserver(mRecyclerViewAdapterDataSetObserver!!)
                        reloadData()
                    }
                }
            }

            LayoutTypeMode.FlowLayout.index->{
                if(adapter is BaseTagAdapter<*>){
                    this.mTagAdapter =adapter as BaseTagAdapter<*>
                    if (mTagAdapter != null && mAdapterDataSetObserver != null) {
                        this.mTagAdapter?.unregisterDataSetObserver(mAdapterDataSetObserver)
                    }
                    if (this.mTagAdapter != null) {
                        mAdapterDataSetObserver = AdapterDataSetObserver()
                        this.mTagAdapter!!.registerDataSetObserver(mAdapterDataSetObserver)
                        reloadData()
                    }
                }
            }

            else->{

            }
        }
    }


    /**
     * 获取适配器
     */
    private fun getAdapter():Any? {
        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{
                return  mRecyclerViewAdapter as Any
            }
            LayoutTypeMode.FlowLayout.index->{
                return  mTagAdapter as Any
            }
            else->{
                return null
            }
        }
    }


    /**
     * 刷新数据
     */
    fun notifyDataSetChanged(){
        when(mLayoutType){
            LayoutTypeMode.RecyclerView.index->{
                mRecyclerViewAdapter?.notifyDataSetChanged()
            }
            LayoutTypeMode.FlowLayout.index->{
                mTagAdapter?.notifyDataSetChanged()
            }
            else->{
            }
        }
        //标识：刷新数据  isNotifyData=true
        isNotifyData=true
    }


    /**
     * 获取列表对象
     * @return RecyclerView
     */
    fun getRecyclerView():RecyclerView{
        return mRecyclerView
    }

    /**
     * 获取流逝布局对象
     * @return FlowLayout
     */
    fun getFlowLayout(): FlowLayout {
        return mFlowLayout
    }


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
            mTvMoreHint.text = foldHint
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
            mTvMoreHint.text = expandHint
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
     *
     * @return Boolean
     */
    fun isCanScroll(): Boolean {
        return mControlScrollView.isCanScroll()
    }

    /**
     * 设置是否退出内部标签容器
     * @param can Boolean
     */
    fun setCanScroll(can: Boolean) {
        mControlScrollView.setCanScroll(can)
    }

    fun setForceFixed(fixed: Boolean) {
        mControlScrollView.setForceFixed(fixed)
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

/******************************************************* set listener *********************************************************************/


    fun setOnTagClickListener(onTagClickListener: OnTagClickListener)=apply {
        this.mOnTagClickListener=onTagClickListener
        if (mTagAdapter != null) {
            reloadData()
        }
    }


    open fun setSelectedListener(selectedListener: OnTagSelectedListener?) {
        this.mSelectedListener = selectedListener
        if (mTagAdapter != null) {
            reloadData()
        }
    }
}