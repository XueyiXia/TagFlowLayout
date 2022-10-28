package com.framework.tagflow

import android.os.Bundle
import android.view.View
import com.framework.tagflow.adapter.TestAdapter
import com.framework.tagflow.bean.SearchHistoryBean
import com.framework.tagflow.databinding.ActivityMainBinding
import com.framework.viewbinding.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var mTestAdapter : TestAdapter

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
    }
}