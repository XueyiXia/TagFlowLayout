package com.framework.tagflow

import android.os.Bundle
import android.view.View
import com.framework.tagflow.adapter.SearchHistoryGridAdapter
import com.framework.tagflow.adapter.SearchHistoryLinearAdapter
import com.framework.tagflow.adapter.TestAdapter
import com.framework.tagflow.bean.SearchHistoryBean
import com.framework.tagflow.databinding.ActivityMainBinding
import com.framework.viewbinding.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var mTestAdapter : TestAdapter

    private lateinit var mSearchHistoryGridAdapter: SearchHistoryGridAdapter

    private lateinit var mSearchHistoryLinearAdapter: SearchHistoryLinearAdapter

    private val mGridSpace:Int=32; //九宫格分割线间隙

    private val mGridSpanCount=3;//九宫格每一行多少个

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
        mViewBinding.multiGridRecyclerTagTitle.text = "RecyclerView 演示(Grid)"
        mViewBinding.multiLinearRecyclerTagTitle.text = "RecyclerView 演示(Linear)"
        mViewBinding.multiFlowTagTitle.text="FlowLayout演示"


        mTestAdapter=TestAdapter(this);
        for(index in 0..10){
            var bean= SearchHistoryBean()
            mTestAdapter.addData(bean)
        }
        mViewBinding.multiFlowTag.setAdapter(mTestAdapter)


        mViewBinding.multiGridRecyclerTag.getRecyclerView().addItemDecoration(GridLayoutItemDecoration(mGridSpanCount,mGridSpace, false))
        mSearchHistoryGridAdapter=SearchHistoryGridAdapter()
        for(index in 0..10){
            var bean= SearchHistoryBean()
            mSearchHistoryGridAdapter.addData(bean)
        }
        mViewBinding.multiGridRecyclerTag.setAdapter(mSearchHistoryGridAdapter)


        mSearchHistoryLinearAdapter=SearchHistoryLinearAdapter()
        for(index in 0..10){
            var bean= SearchHistoryBean()
            mSearchHistoryLinearAdapter.addData(bean)
        }
        mViewBinding.multiLinearRecyclerTag.setAdapter(mSearchHistoryLinearAdapter)
    }
}