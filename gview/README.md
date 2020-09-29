# 自定义View
## Maven
```
<dependency>
  <groupId>com.guc.gview</groupId>
  <artifactId>gview</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```
## Gradle
```
implementation 'com.guc.gview:gview:1.0.1'
```
## LedTextView  LED数字显示效果

## ScaleView  刻度尺工具，可显示温度和电流
### 自定义属性
* scaleType:刻度尺类型（temperature：温度计，galvanometer：电流计）
* borderColor：边框颜色
* borderWidth：边框宽度
* pillarDownColor：柱子下部分颜色
* pillarUpColor：柱子上部分颜色
* pillarWidth：柱子宽度
* tickCount：刻度数量
* defaultValue：初始值
* defaultMaxValue：初始最大值
* defaultMinValue：初始最小值
### 布局引入
```
        <com.guc.gview.ScaleView
            android:id="@+id/scaleView"
            android:layout_width="50dp"
            android:layout_height="100dp"
            android:textSize="12sp"
            app:scaleType="galvanometer"
            app:borderColor="@color/colorPrimary"
            app:pillarDownColor="@color/colorPrimaryDark"
            app:pillarUpColor="#ffffff"
            app:tickCount="10"
            app:defaultMaxValue="100"
            app:defaultMinValue="0"
            app:defaultValue="20"
            />
```
### 代码中修改最大值/最小值/设置当前值
```
    private fun testScaleView() {
        scaleView.type=ScaleView.SCALE_TYPE_TEMPERATURE
        scaleView.minValue=35
        scaleView.maxValue=42
        scaleView.value = 36.5f
    }
```
## PanelView 仪表盘
### 自定义属性
* arcColor：仪表盘色带选中颜色
* arcUnColor：仪表盘色带未选中颜色
* arcWidth：仪表盘色带宽度
* tickCount：刻度线数量
* tickColor：刻度线颜色
* pointerColor：指针颜色
* outerLineColor：最外层线颜色
* innerLineColor：内层线颜色
* pointerType：指针样式（line：线状，pointer：指针状）
* lineSpace：外层线间隔
### 布局引入
```
    <com.guc.gview.PanelView
        android:id="@+id/panelView2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="10dp"
        app:arcColor="#46B31D"
        app:arcUnColor="#C6E89F"
        app:innerLineColor="@color/colorPrimary"
        app:lineSpace="60"
        app:outerLineColor="@color/colorPrimaryDark"
        app:pointerColor="@color/colorPrimaryDark"
        app:pointerType="line"
        app:tickColor="#46B31D" />
```
### 代码中修改百分比
* percent：设置百分比
* maxValue：设置最大值
* needBulge：设置突出效果
* pointerColor：指针颜色

* playAnim()：播放动画
* setValue(value: Float)：设置值，自动转为百分比并播放动画

```
   private fun testPanelView() {
        panelView.percent = 50
        panelView.needBulge = true
    }
```
### 效果图
<img src="https://github.com/icookingcode/GucChartView/blob/master/snapshoot/Screenshot_1600943679.png"  height="576" width="306"/>

## FolderView  折叠/展开控件
### 自定义属性
* isTextVisible：文字是否显示
* android:textColor：字体颜色
* android:textSize：字体大小
* arrowDownDrawable：展开操作图标
* arrowUpDrawable：折叠操作图标
* controlledView：被控制的View的Id
* tipFold：展开提示语
* tipUnfold：折叠提示语
### 布局引入
```
    <com.guc.gview.FolderView
        android:id="@+id/folderView"
        android:layout_margin="10dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="12sp"
        app:isTextVisible="true"
        app:controlledView="@+id/llTop"/>
```
### 代码中设置参数
* folderType：true:展开  false:折叠
* textColor：文字颜色
* textSize：文字大小
* isTextVisible：文字是否可见
* tipFold：展开操作提示语
* tipUnfold：折叠操作提示语

## ExpandableTextView  多行文本显示（折叠、展开）
### 自定义属性
* etv_MaxLinesOnShrink：折叠状态下显示文本行数
* etv_EllipsisHint：折叠状态省略样式， 默认 ...
* etv_ToExpandHint：展开状态下操作提示语，默认 收起
* etv_ToShrinkHint：折叠状态下操作提示语，默认 显示更多
* etv_GapToExpandHint：展开状态下文本语操作语间填充
* etv_GapToShrinkHint：折叠状态下文本语操作语间填充
* etv_EnableToggle：点击View是否切换折叠/展开
* etv_IsShowExpandHint：是否显示展开操作提示语
* etv_IsShowShrinkHint：是否显示折叠操作提示语
* etv_ToExpandHintColor：展开操作提示语颜色
* etv_ToShrinkHintColor：折叠操作提示语颜色
* etv_ToExpandHintColorBgPressed：开操作提示语点击背景色
* etv_ToShrinkHintColorBgPressed：折叠作提示语点击背景色
* etv_InitState：初始状态 shrink：折叠 expand：展开
### 布局引入
```
    <com.guc.gview.textview.ExpandableTextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/story"
        app:etv_IsShowExpandHint="false"
        app:etv_IsShowShrinkHint="false"
        app:etv_EnableToggle="true"/>
```
注意：etv_IsShowExpandHint | etv_IsShowShrinkHint 设置为false时，etv_EnableToggle必须设置true,否则无法切换折叠/展开状态
### 提供的属性及方法
* currentState：设置状态 STATE_SHRINK/STATE_EXPAND
* getState()：获取当前状态

## LineLoadingDrawable  线条加载动画
## CoffeeDrawable  搅拌咖啡加载动画

## SignNameView  签名控件
### 自定义属性
* lineWith：签名线宽度
* bgColor：签名背景色
* lineColor：签名字体颜色
### 提供的方法
* clean()：清除签名
* getSignBitmap()：获取签名bitmap
* saveSign(path):保存签名到指定位置

## RiseNumberTextView  带增长动画的TextView
### 提供的方法
* withNumber(number: Float, flag: Boolean = true)：设置数值
* withNumber(number: Float, flag: Boolean = true)：设置数值
* setDuration(duration:Long)：设置动画持续时间
* start():启动动画
调用示例
```
riseNumber.withNumber(9999999).start()
```

#关于我
Name: Guchao
Email: happygc913@gmail.com / happygc@126.com
CSDN: [snow_lyGirl](https://blog.csdn.net/qq_31028313)
GitHub: [GuchaoGit](https://github.com/GuchaoGit?tab=repositories)
Gitee:[GuChaoGitee](https://gitee.com/guchaogitee/projects)
加入QQ群:128937635