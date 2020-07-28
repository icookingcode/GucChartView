# GucChartView
自定义图表
## PieChartView
### 属性
* bgColor  背景颜色
* lineWith  指示线宽度
* radius  大圆半径
* radiusInner  内圆半径
* radiusCenterPoint  指示圆点的半径
* innerCircleColor  内圆填充颜色
* textSize4Describe  描述字体大小
* textSize4Score  数字字体大小
* textColor4Describe  描述字体颜色
* textColor4Score  数字字体颜色
* isDrawCenterText  是否绘制中间分数总和
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
                PieChartView.PieData(45f, "数据库", Color.GREEN),
                PieChartView.PieData(65f, "ftp", Color.YELLOW),
                PieChartView.PieData(60f, "专线接入", Color.RED),
                PieChartView.PieData(35f, "设备采集", Color.BLACK),
                PieChartView.PieData(35f, "其他", Color.MAGENTA)
        )
        pieChartView.setData(data)
    }
```
### 效果图
<img src="https://github.com/icookingcode/GucChartView/blob/master/snapshoot/Screenshot_1595903809.png"  height="576" width="306"/>