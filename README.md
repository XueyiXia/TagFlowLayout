# MultiTagFlowLayout
多样可折叠和展开的标签控件 ，MultiTagFlowLayout相当于一个容器一样， 支持RecyclerView和FlowLayout组件，RecyclerView 只支持垂直九宫格和垂直列表，和RecyclerView 使用一样；FlowLayout 流式布局组件，支持标签，标签单选，标签多选等功能！！！

##先看两张图

![image](https://user-images.githubusercontent.com/25949241/198920311-11f5a142-2c6b-44b7-9f4b-da2a2c5a79be.png)
![image](https://user-images.githubusercontent.com/25949241/198920373-5bb678d4-20ff-4602-8e3d-5e388e05d5f0.png)

如果需要看gif 图片，看这里

![ezgif com-gif-maker](https://user-images.githubusercontent.com/25949241/198842982-9eabfb25-cdc3-45b3-832a-d1aaad4b84c0.gif)


##How to use Add this to your build.gradle:

dependencies {

		implementation 'com.github.XueyiXia:MultiTagFlowLayout:v1.0.1' //v1.0.0
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



##默认tag样式

1.DefaultTagView (默认实心tag)

2.ColorfulTagView (彩色背景实心tag)

3.StrokeTagView （空心带边框的tag）

4.ColorfulStrokeTagView （空心彩色边框tag）

自定义tag，继承以上tag或者自定义View





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
        </attr>
    </declare-styleable>
    
    
    
 ![QQ截图20221030005116](https://user-images.githubusercontent.com/25949241/198843533-2fa726b8-ec58-4c4c-96e4-6c8a836661da.png)



## 最后，代码是最好的老师，如果有兴趣，可以下载demo 看看，如果这个控件能帮助到你，请右上角点击个星星吧！！！Thanks you so much .........END
