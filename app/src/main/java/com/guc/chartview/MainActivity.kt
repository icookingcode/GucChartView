package com.guc.chartview

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guc.gchartview.model.PieData
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