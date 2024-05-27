package com.framework.tagflow

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.framework.tagflow.adapter.TestAdapter
import com.framework.tagflow.adapter.TestGridAdapter
import com.framework.tagflow.adapter.TestLinearAdapter
import com.framework.tagflow.bean.BaseTagBean
import com.framework.tagflow.bean.TestBean
import com.framework.tagflow.interfac.OnTagClickListener
import com.framework.tagflow.interfac.OnTagSelectedListener
import com.framework.tagflow.viewholder.BaseQuickAdapter
import com.framework.viewbinding.base.BaseActivity
import com.tagflow.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {


    private lateinit var mTestGridAdapter: TestGridAdapter

    private lateinit var mTestLinearAdapter: TestLinearAdapter

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
        mTestLinearAdapter=TestLinearAdapter()
        for(index in 0..5){
            var bean= TestBean()
            mTestLinearAdapter.addData(bean)
        }
        mViewBinding.multiLinearRecyclerTag.setAdapter(mTestLinearAdapter)
        mTestLinearAdapter.setOnItemClickListener(object :com.framework.tagflow.listener.OnItemClickListener{

            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                Toast.makeText(this@MainActivity,"onItemClick事件-->>:$position",Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun initGridAdapter(){
        mViewBinding.multiGridRecyclerTag.getRecyclerView().addItemDecoration(GridLayoutItemDecoration(mGridSpanCount,mGridSpace, false))
        mTestGridAdapter= TestGridAdapter()
        for(index in 0..10){
            val bean= TestBean()
            mTestGridAdapter.addData(bean)
        }
        mViewBinding.multiGridRecyclerTag.setAdapter(mTestGridAdapter)
        mTestGridAdapter.setOnItemClickListener(object :com.framework.tagflow.listener.OnItemClickListener{

            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                Toast.makeText(this@MainActivity,"onItemClick事件-->>:$position",Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun initTagFlowAdapter(){
        val dataList:MutableList<TestBean> = mutableListOf()
        val mTestAdapter=TestAdapter(this,true);
        for(index in 0..50){
            val bean= TestBean()
            bean.setTitle("ADD TAG$index")
            dataList.add(bean)

        }
        mTestAdapter.addAllList(dataList)
        mViewBinding.multiFlowTag.setAdapter(mTestAdapter)
        mViewBinding.multiFlowTag.setOnTagClickListener(object :OnTagClickListener{
            override fun onClick(view: View?, position: Int) {
                Toast.makeText(this@MainActivity,"点击事件-->>:$position",Toast.LENGTH_SHORT).show()
            }

            override fun onLongClick(view: View?, position: Int) {
                Toast.makeText(this@MainActivity,"长按事件-->>:$position",Toast.LENGTH_SHORT).show()
            }
        })

        mViewBinding.multiFlowTag.setItemModel(MultiTagFlowLayout.ITEM_MODEL_SELECT)
        mViewBinding.multiFlowTag.setSelectedListener(object :OnTagSelectedListener{

            override fun selected(view: View?, position: Int, selected: List<BaseTagBean?>?) {

                Toast.makeText(this@MainActivity,"selected事件-->>:$position",Toast.LENGTH_SHORT).show()
            }

            override fun unSelected(view: View?, position: Int, selected: List<BaseTagBean?>?) {
                Toast.makeText(this@MainActivity,"unSelected事件-->>:$position",Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun initTagFlowAdapterSelf(){
        val dataList:MutableList<TestBean> = mutableListOf()
        val mTestAdapter=TestAdapter(this,false);
        for(index in 0..100){
            val bean= TestBean()
            bean.setTitle("ADD TAG BY $index")
            dataList.add(bean)
        }
        mTestAdapter.addAllList(dataList)
        mViewBinding.multiFlowTagSelf.setAdapter(mTestAdapter)
        mViewBinding.multiFlowTagSelf.setOnTagClickListener(object :OnTagClickListener{
            override fun onClick(view: View?, position: Int) {
                Toast.makeText(this@MainActivity,"点击事件-->>:$position",Toast.LENGTH_SHORT).show()
            }

            override fun onLongClick(view: View?, position: Int) {
                Toast.makeText(this@MainActivity,"长按事件-->>:$position",Toast.LENGTH_SHORT).show()
            }
        })

    }
}