# 自定义View
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