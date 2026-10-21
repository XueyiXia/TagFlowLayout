# MultiTagFlowLayout

支持 RecyclerView 垂直线性布局、RecyclerView 九宫格和 FlowLayout 的折叠/展开标签控件，适用于标签筛选、兴趣选择、分类展示等场景。

一个简洁、可扩展、可定制的标签容器组件，可在不改动业务代码的情况下切换不同布局模式。

## 特性

- 支持 RecyclerView 垂直线性布局
- 支持 RecyclerView 九宫格布局
- 支持 FlowLayout
- 支持标签折叠和展开
- 支持点击、长按、选中/取消选中回调
- 支持自定义 Tag 样式
- 支持自定义 XML 属性
- 支持生命周期绑定和取消请求

## 效果展示

### 主场景展示

<div align="center">
  <img src="https://github.com/XueyiXia/TagFlowLayout/assets/25949241/d5402aa2-469e-4df1-9af9-12f0f87ed85b" width="350" />
  <img src="https://github.com/XueyiXia/TagFlowLayout/assets/25949241/f9ef28bb-9895-4d2d-b753-a07e7189095b" width="350" />
</div>

### 无数据展示

<div align="center">
  <img width="350" alt="image" src="https://github.com/user-attachments/assets/ebfb091f-93fa-4639-a26b-d21e35c7f3d9" />
</div>

### 动图演示

![device-2023-07-26-122816](https://github.com/XueyiXia/TagFlowLayout/assets/25949241/3cabc90b-be4f-4c6a-aba4-2bf6433b2fee)

### 额外展示

![QQ截图20221030005116](https://user-images.githubusercontent.com/25949241/198843533-2fa726b8-ec58-4c4c-96e4-6c8a836661da.png)

---

## 组件说明

`MultiTagFlowLayout` 本质上是一个标签容器，内部支持切换不同布局模式：

- `FlowLayout`
- `RecyclerView + LinearLayoutManager`
- `RecyclerView + GridLayoutManager`

它适用于：

- 标签筛选
- 热门搜索词
- 兴趣选择
- 分类展示
- 用户标签管理

---

## 集成方式

### Gradle

```gradle
dependencies {
    implementation 'com.github.XueyiXia:TagFlowLayout:v1.0.7'
}
```

---

## 使用方法

### 1. 定义 XML

```xml
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
    app:tagsVerticalSpace="@dimen/dp_5" />
```

### 2. 初始化适配器

```kotlin
mTestAdapter = TestAdapter(this)
for (index in 0..10) {
    val bean = TestBean()
    mTestAdapter.addData(bean)
}

mViewBinding.multiFlowTag.setAdapter(mTestAdapter)
```

### 3. 点击事件

```kotlin
mViewBinding.multiFlowTag.setOnTagClickListener(object : OnTagClickListener {
    override fun onClick(view: View?, position: Int) {
        Toast.makeText(this@MainActivity, "点击事件-->>:$position", Toast.LENGTH_SHORT).show()
    }

    override fun onLongClick(view: View?, position: Int) {
        Toast.makeText(this@MainActivity, "长按事件-->>:$position", Toast.LENGTH_SHORT).show()
    }
})
```

### 4. 选中状态回调

```kotlin
mViewBinding.multiFlowTag.setItemModel(MultiTagFlowLayout.ITEM_MODEL_SELECT)
mViewBinding.multiFlowTag.setSelectedListener(object : OnTagSelectedListener {
    override fun selected(view: View?, position: Int, selected: List<BaseTagBean?>?) {
        Toast.makeText(this@MainActivity, "selected事件-->>:$position", Toast.LENGTH_SHORT).show()
    }

    override fun unSelected(view: View?, position: Int, selected: List<BaseTagBean?>?) {
        Toast.makeText(this@MainActivity, "unSelected事件-->>:$position", Toast.LENGTH_SHORT).show()
    }
})
```

### 5. 自定义子 View 点击事件

```kotlin
mViewBinding.multiFlowTagSelf.addOnTagChildClickListener(com.tagflow.R.id.flFlagClose) { adapter, view, position ->
    Toast.makeText(this@MainActivity, "点击了子tag view-->>:$position", Toast.LENGTH_SHORT).show()
}
```

---

## 默认 Tag 样式

内置支持以下几种标签样式：

1. `DefaultTagView`：默认实心标签
2. `ColorfulTagView`：彩色背景实心标签
3. `StrokeTagView`：空心边框标签
4. `ColorfulStrokeTagView`：空心彩色边框标签

可以直接使用，也可以继承上面的 View 进行自定义扩展。

---

## 自定义属性

```xml
<declare-styleable name="TagFlowLayout">
    <attr name="tagsHorizontalSpace" format="dimension" />
    <attr name="tagsVerticalSpace" format="dimension" />
    <attr name="animationDuration" format="integer" />
    <attr name="hasMore" format="boolean" />
    <attr name="foldHint" format="reference|string" />
    <attr name="expandHint" format="reference|string" />

    <attr name="layout_type" format="reference">
        <enum name="RecyclerView" value="0" />
        <enum name="FlowLayout" value="1" />
    </attr>

    <attr name="layoutManager_Mode" format="reference">
        <enum name="LinearLayoutManager" value="0" />
        <enum name="GridLayoutManager" value="1" />
    </attr>
</declare-styleable>
```

---

## 适用场景

- 会员标签筛选
- 热门搜索词展示
- 兴趣/爱好标签管理
- 分类筛选列表
- 商品/文章/用户标签展示

---

## 说明

这个控件本身是一个轻量型标签展示组件，重点在于：

- 展开/收起流畅
- 布局灵活
- 能适配常见筛选需求
- 支持多种展示模式

如果你有更好的交互方案或者需要扩展更多场景，欢迎提交 issue 或 PR。

---

## 最后说明

如果这个控件对你有帮助，欢迎在 GitHub 上给项目点个 Star。你的支持是我持续优化和维护这个组件的动力。

Thanks you so much ... END
