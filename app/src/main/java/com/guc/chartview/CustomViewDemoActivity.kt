package com.guc.chartview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.guc.gview.ScaleView
import com.guc.gview.loading.CoffeeDrawable
import com.guc.gview.loading.LineLoadingDrawable
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
        testRiseNumber()
        testScaleView()
        testPanelView()
        testExpandTextView()
        testLineLoading()
        testSignView()
    }

    private fun testRiseNumber() {
        riseNumber.withNumber(9999999).start()
    }

    private fun testSignView() {
        btnSaveSign.setOnClickListener {
            if (signNameView.hasSign()) {
                val success =
                    signNameView.saveSign("$externalCacheDir/${System.currentTimeMillis()}.jpeg")
                if (success) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "未签名", Toast.LENGTH_LONG).show()
            }
        }
        btnClearSign.setOnClickListener {
            signNameView.clean()
        }
    }

    private fun testLineLoading() {
        val lineLoading = LineLoadingDrawable()
        ivLineLoading.setImageDrawable(lineLoading)
        ivLineLoading.post {
            lineLoading.start()
        }
        val coffeeDrawable = CoffeeDrawable.create(ivCoffeeLoading)
        coffeeDrawable.progress = 1f
        ivCoffeeLoading.postDelayed({
            coffeeDrawable.start()
        }, 1000)
    }

    private fun testExpandTextView() {
        etv.currentState = ExpandableTextView.STATE_SHRINK
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