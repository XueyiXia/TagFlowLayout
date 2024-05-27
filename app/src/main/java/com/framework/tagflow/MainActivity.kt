package com.framework.tagflow

import android.os.Bundle
import android.view.View
import com.framework.tagflow.adapter.SearchHistoryGridAdapter
import com.framework.tagflow.adapter.SearchHistoryLinearAdapter
import com.framework.tagflow.adapter.TestAdapter
import com.framework.tagflow.bean.BaseTagBean
import com.framework.tagflow.bean.SearchHistoryBean
import com.framework.tagflow.interfac.OnTagClickListener
import com.framework.tagflow.interfac.OnTagSelectedListener
import com.framework.tagflow.utils.ToastHelper
import com.framework.tagflow.viewholder.BaseQuickAdapter
import com.framework.viewbinding.base.BaseActivity
import com.tagflow.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {


    private lateinit var mSearchHistoryGridAdapter: SearchHistoryGridAdapter

    private lateinit var mSearchHistoryLinearAdapter: SearchHistoryLinearAdapter

    private val mGridSpace:Int=32; //九宫格分割线间隙

    private val mGridSpanCount=3;//九宫格每一行多少个

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
        mViewBinding.multiGridRecyclerTagTitle.text = "RecyclerView 演示(Grid)"
        mViewBinding.multiLinearRecyclerTagTitle.text = "RecyclerView 演示(Linear)"
        mViewBinding.multiFlowTagTitle.text="FlowLayout演示（自带布局）"
        mViewBinding.multiFlowTagTitleSelf.text="FlowLayout演示（自定义布局）"

        initLinearAdapter()

        initGridAdapter()

        initTagFlowAdapter()

        initTagFlowAdapterSelf()

    }





    private fun initLinearAdapter(){
        mSearchHistoryLinearAdapter=SearchHistoryLinearAdapter()
        for(index in 0..5){
            var bean= SearchHistoryBean()
            mSearchHistoryLinearAdapter.addData(bean)
        }
        mViewBinding.multiLinearRecyclerTag.setAdapter(mSearchHistoryLinearAdapter)
        mSearchHistoryLinearAdapter.setOnItemClickListener(object :com.framework.tagflow.listener.OnItemClickListener{

            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                ToastHelper.showMsgShort(this@MainActivity, "onItemClick事件-->>:$position")
            }
        })
    }


    private fun initGridAdapter(){
        mViewBinding.multiGridRecyclerTag.getRecyclerView().addItemDecoration(GridLayoutItemDecoration(mGridSpanCount,mGridSpace, false))
        mSearchHistoryGridAdapter=SearchHistoryGridAdapter()
        for(index in 0..10){
            val bean= SearchHistoryBean()
            mSearchHistoryGridAdapter.addData(bean)
        }
        mViewBinding.multiGridRecyclerTag.setAdapter(mSearchHistoryGridAdapter)
        mSearchHistoryGridAdapter.setOnItemClickListener(object :com.framework.tagflow.listener.OnItemClickListener{

            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                ToastHelper.showMsgShort(this@MainActivity, "onItemClick事件-->>:$position")
            }
        })
    }


    private fun initTagFlowAdapter(){
        val dataList:MutableList<SearchHistoryBean> = mutableListOf()
        val mTestAdapter=TestAdapter(this,true);
        for(index in 0..50){
            val bean= SearchHistoryBean()
            bean.setTitle("ADD TAG$index")
            dataList.add(bean)

        }
        mTestAdapter.addAllList(dataList)
        mViewBinding.multiFlowTag.setAdapter(mTestAdapter)
        mViewBinding.multiFlowTag.setOnTagClickListener(object :OnTagClickListener{
            override fun onClick(view: View?, position: Int) {
                ToastHelper.showMsgShort(this@MainActivity, "点击事件-->>:$position")
            }

            override fun onLongClick(view: View?, position: Int) {
                ToastHelper.showMsgShort(this@MainActivity, "长按事件-->>:$position")
            }
        })

        mViewBinding.multiFlowTag.setItemModel(MultiTagFlowLayout.ITEM_MODEL_SELECT)
        mViewBinding.multiFlowTag.setSelectedListener(object :OnTagSelectedListener{

            override fun selected(view: View?, position: Int, selected: List<BaseTagBean?>?) {
                ToastHelper.showMsgShort(this@MainActivity, "selected事件-->>:$position")
            }

            override fun unSelected(view: View?, position: Int, selected: List<BaseTagBean?>?) {
                ToastHelper.showMsgShort(this@MainActivity, "unSelected事件-->>:$position")
            }
        })
    }


    private fun initTagFlowAdapterSelf(){
        val dataList:MutableList<SearchHistoryBean> = mutableListOf()
        val mTestAdapter=TestAdapter(this,false);
        for(index in 0..50){
            val bean= SearchHistoryBean()
            bean.setTitle("ADD TAG BY $index")
            dataList.add(bean)
        }
        mTestAdapter.addAllList(dataList)
        mViewBinding.multiFlowTagSelf.setAdapter(mTestAdapter)
        mViewBinding.multiFlowTagSelf.setOnTagClickListener(object :OnTagClickListener{
            override fun onClick(view: View?, position: Int) {
                ToastHelper.showMsgShort(this@MainActivity, "点击事件-->>:$position")
            }

            override fun onLongClick(view: View?, position: Int) {
                ToastHelper.showMsgShort(this@MainActivity, "长按事件-->>:$position")
            }
        })

    }
}