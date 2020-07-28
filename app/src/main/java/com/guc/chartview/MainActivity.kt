package com.guc.chartview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.guc.gchartview.PieChartView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadPieChartData()
    }

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
}