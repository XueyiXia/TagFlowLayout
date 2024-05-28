# MultiTagFlowLayout
多样可折叠和展开的标签控件 ，MultiTagFlowLayout相当于一个容器一样， 支持RecyclerView和FlowLayout组件，RecyclerView 只支持垂直九宫格和垂直列表，和RecyclerView 使用一样；FlowLayout 流式布局组件，支持标签，标签单选，标签多选等功能！！！

##先看两张图

<img src="https://github.com/XueyiXia/TagFlowLayout/assets/25949241/d5402aa2-469e-4df1-9af9-12f0f87ed85b" width="350px">


<img src="https://github.com/XueyiXia/TagFlowLayout/assets/25949241/f9ef28bb-9895-4d2d-b753-a07e7189095b" width="350px">






## Gif图：

![device-2023-07-26-122816](https://github.com/XueyiXia/TagFlowLayout/assets/25949241/3cabc90b-be4f-4c6a-aba4-2bf6433b2fee)





## How to use Add this to your build.gradle:

dependencies {

		implementation 'com.github.XueyiXia:TagFlowLayout:v1.0.5' 
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
            var bean= TestBean()
            mTestAdapter.addData(bean)
        }
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



## 默认tag样式

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
