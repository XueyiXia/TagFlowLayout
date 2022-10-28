package com.framework.tagflow

import android.os.Bundle
import android.view.View
import com.framework.tagflow.databinding.ActivityMainBinding
import com.framework.viewbinding.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {


    override fun initView(rootView: View, savedInstanceState: Bundle?) {
        mViewBinding.multiGridRecyclerTagTitle.text = "RecyclerView 演示(Grid)"
        mViewBinding.multiLinearRecyclerTagTitle.text = "RecyclerView 演示(Linear)"
        mViewBinding.multiFlowTagTitle.text="Flow演示"
    }
}