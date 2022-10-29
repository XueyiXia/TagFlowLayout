# MultiTagFlowLayout
多样可折叠和展开的标签控件 （RecyclerView和FlowLayout），RecyclerView 只支持垂直九宫格和垂直列表，单选，多选，多行展开缩回的功能

##先看两张图

![Screenshot_20221030_003538](https://user-images.githubusercontent.com/25949241/198842906-1c0a98a1-384d-4a5b-997f-9699a7c1f569.png)
![Screenshot_20221030_003613](https://user-images.githubusercontent.com/25949241/198842918-fc3a1e11-dc3d-4bbc-81b7-8ffbd1958a67.png)

如果需要看gif 图片，看这里

![ezgif com-gif-maker](https://user-images.githubusercontent.com/25949241/198842982-9eabfb25-cdc3-45b3-832a-d1aaad4b84c0.gif)


##How to use Add this to your build.gradle:

dependencies {

		implementation 'com.github.XueyiXia:MultiTagFlowLayout:version' //v1.0.0
	}
    
    
1. 定义xml布局

            <com.framework.tagflow.MultiTagFlowLayout
                android:id="@+id/multi_flow_tag"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="@dimen/dp_10"
                android:layout_marginStart="@dimen/dp_10"
                android:layout_marginEnd="@dimen/dp_10"
                app:expandHint="@string/expandHint"
                app:foldHint="@string/foldHint"
                app:hasMore="true"
                app:layout_type="FlowLayout"
                app:tagsHorizontalSpace="@dimen/dp_5"
                app:tagsVerticalSpace="@dimen/dp_5"/>

2. 初始化

        mTestAdapter=TestAdapter(this);
        for(index in 0..10){
            var bean= SearchHistoryBean()
            mTestAdapter.addData(bean)
        }
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
    
  3可选项(部分属性可直接在xml布局中指定)

      <declare-styleable name="TagFlowLayout">
        <attr name="tagsHorizontalSpace" format="dimension" />
        <attr name="tagsVerticalSpace" format="dimension" />
        <attr name="animationDuration" format="integer" />
        <attr name="hasMore" format="boolean" />
        <attr name="foldHint" format="reference|string" />
        <attr name="expandHint" format="reference|string" />

        <!--布局类型，列表或者-->
        <attr name="layout_type" format="reference">
            <enum name="RecyclerView" value="0" />
            <enum name="FlowLayout" value="1" />
        </attr>

        <!--LayoutManager 模式-->
        <attr name="layoutManager_Mode" format="reference">
            <enum name="LinearLayoutManager" value="0"/>
            <enum name="GridLayoutManager" value="1"/>
            <enum name="StaggeredGridLayoutManager" value="2"/>
        </attr>
    </declare-styleable>
    
    
![image](https://user-images.githubusercontent.com/25949241/198843491-3be93e6d-c2a2-416e-9c5e-d49909ad717d.png)




## 最后，代码是最好的老师，如果有兴趣，可以下载demo 看看，希望这个控件能帮助到你.........END
