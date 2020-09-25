package com.guc.chartview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guc.gview.ScaleView
import com.guc.gview.textview.ExpandableTextView
import kotlinx.android.synthetic.main.activity_custom_view_demo.*

/**
 * Created by Guc on 2020/9/24.
 * Description：自定义View
 */
class CustomViewDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view_demo)

        testScaleView()
        testPanelView()
        testExpandTextView()
    }

    private fun testExpandTextView() {
        etv.currentState = ExpandableTextView.STATE_EXPAND
    }

    private fun testPanelView() {
        panelView.percent = 20
        panelView2.setValue(50f)
        panelView2.needBulge = true
    }

    private fun testScaleView() {
        scaleView.type = ScaleView.SCALE_TYPE_TEMPERATURE
        scaleView.minValue = 35
        scaleView.maxValue = 42
        scaleView.value = 36.5f
    }
}