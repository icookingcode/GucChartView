package com.guc.chartview

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.guc.gsideslip.STATE_EXPANDED
import com.guc.gsideslip.STATE_MOVING
import com.guc.gsideslip.SideSlipSurfaceView
import com.guc.gsideslip.SideSlipView
import kotlinx.android.synthetic.main.activity_side_slip.*

private const val TAG = "SideSlipActivity"

/**
 * Created by Guc on 2020/9/23.
 * Description：侧滑菜单
 */

class SideSlipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_side_slip)
        sideSlipGuideView.addGuides(
            R.drawable.bg_guide_1,
            R.drawable.bg_guide_2,
            R.drawable.bg_guide_3
        )
        sideSlipGuideView.onStart = {
            Toast.makeText(this, "开启应用", Toast.LENGTH_LONG).show()
        }

        sideSlipSurfaceView.setImageResource(R.drawable.bg_guide_1)

        sideSlipSurfaceView.setOnStateChangedListener(object :
            SideSlipSurfaceView.OnStateChangedListener {
            override fun onStateChanged(state: Int) {
                if (state == STATE_EXPANDED) {
                    Log.e(TAG, "展开")
                } else if (state == STATE_MOVING) {
                    Log.e(TAG, "滑动")
                } else {
                    Log.e(TAG, "关闭")
                }
            }

        })

        sideSlipView.setImageResource(R.drawable.bg_guide_1)

        sideSlipView.setOnStateChangedListener(object : SideSlipView.OnStateChangedListener {
            override fun onStateChanged(state: Int) {
                if (state == STATE_EXPANDED) {
                    Log.e(TAG, "展开")
                } else if (state == STATE_MOVING) {
                    Log.e(TAG, "滑动")
                } else {
                    Log.e(TAG, "关闭")
                }
            }

        })
    }
}