package com.guc.chartview

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guc.gchartview.model.PieData
import com.guc.kframe.ui.DetailInBrowserActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadPieChartData()

        btnNaviDemo.setOnClickListener {
            startActivity(Intent(this, NaviMapActivity::class.java))
        }

        btnSideSlip.setOnClickListener {
            startActivity(Intent(this, SideSlipActivity::class.java))
        }
        btnGView.setOnClickListener {
            startActivity(Intent(this, CustomViewDemoActivity::class.java))
        }
        btnECharts.setOnClickListener {
            DetailInBrowserActivity.showDetail(
                this,
                "https://echarts.apache.org/examples/zh/editor.html?c=global-population-bar3d-on-globe&gl=1",
                "ECharts"
            )
        }
    }

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
}