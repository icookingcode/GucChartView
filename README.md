# gchartview
自定义图表
## PieChartView
### 属性
* bgColor  背景颜色
* lineWith  指示线宽度
* lineStyle  指示线风格 match:自适应  fixed:固定宽度
* lineOffsetX  指示线X轴偏移量
* radius  大圆半径
* radiusInner  内圆半径
* radiusCenterPoint  指示圆点的半径
* centerPointPosition  指示圆点位置 inner:靠近饼图  outer:远离饼图
* centerPointStyle  指示圆点风格 solid:实心  outer:空心
* centerPointOffset  指示圆点距离饼图的偏移量（等0 时指示线与饼状图连接在一起）
* innerCircleColor  内圆填充颜色
* textSize4Describe  描述字体大小
* textSize4Score  数字字体大小
* textColor4Describe  描述字体颜色
* textColor4Score  数字字体颜色
* isDrawCenterText  是否绘制中间分数总和
* startAngle  开始绘制饼图的起始角度
* rotateEnable  是否可以旋转
### xml引用
```
 <com.guc.gchartview.PieChartView
        android:id="@+id/pieChartView"
        android:layout_width="match_parent"
        android:layout_height="200dp"/>
```
### 设置数据
```
    private fun loadPieChartData() {
            val data = listOf(
                PieData(45f, "数据库", Color.GREEN),
                PieData(65f, "ftp", Color.YELLOW),
                PieData(60f, "专线接入", Color.RED),
                PieData(35f, "设备采集", Color.BLACK),
                PieData(35f, "其他", Color.MAGENTA)
            )
            pieChartView.setData(data)
    }
```
### 效果图
<img src="https://github.com/icookingcode/GucChartView/blob/master/snapshoot/Screenshot_1595903809.png"  height="576" width="306"/>
<img src="https://github.com/icookingcode/GucChartView/blob/master/snapshoot/Screenshot_1596012210.png"  height="576" width="306"/>

# gsideslip 流体控件
## SideSlipView
### xml引用
```
    <com.guc.gsideslip.SideSlipView
        android:id="@+id/sideSlipView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
### 使用
```
    sideSlipView.setImageResource(R.drawable.bg_guide_1)
    
    sideSlipView.setOnStateChangedListener(object : SideSlipView.OnStateChangedListener{
        override fun onStateChanged(state: Int) {
            if (state == STATE_EXPANDED) {
                Log.e(TAG,"展开")
            else if (state == STATE_MOVING) {
                Log.e(TAG,"滑动")
            } else {
                Log.e(TAG,"关闭")
            }
        }
    
    })
```
## SideSlipSurfaceView 用法同SideSlipView
## SideSlipGuideView  炫酷引导页
### xml引用
```
    <com.guc.gsideslip.SideSlipGuideView
        android:id="@+id/sideSlipGuideView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
### 使用
```
   sideSlipGuideView.addGuides(R.drawable.bg_guide_1,R.drawable.bg_guide_2,R.drawable.bg_guide_3)
   sideSlipGuideView.onStart = {
        Toast.makeText(this,"开启应用",Toast.LENGTH_LONG).show()  //点击最后一页进入应用
   }
```

# gview
