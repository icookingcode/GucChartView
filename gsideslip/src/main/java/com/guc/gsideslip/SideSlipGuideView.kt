package com.guc.gsideslip

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import kotlin.math.absoluteValue

private const val TAG = "SideSlipGuideView"

/**
 * Created by guc on 2020/9/23.
 * Description：引导页
 */
class SideSlipGuideView : RelativeLayout {
    var onStart: (() -> Unit)? = null
    private val views = ArrayList<SideSlipView>()

    private var curShowPosition: Int = -1

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    }

    fun addGuides(vararg resIds: Int) {
        resIds.iterator().forEach {
            val flowView = SideSlipView(context)
            views.add(flowView)
            flowView.setImageResource(it)
            val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            flowView.layoutParams = layoutParams
            flowView.visibility = View.VISIBLE
            flowView.setOnStateChangedListener(object : SideSlipView.OnStateChangedListener {
                override fun onStateChanged(state: Int) {
                    when (state) {
                        STATE_EXPANDED -> {
                            showNext()
                        }
                        STATE_MOVING -> {
                        }
                        else -> {
                            hideNext()
                        }
                    }
                }
            })
            addView(flowView)
        }
        handleLastPage()
        Handler().postDelayed({
            views.iterator().withIndex().forEach {
                if (it.index == 0) {
                    it.value.showWithAnim()
                } else {
                    it.value.hideWithAnim()
                }
            }
            views[0].startExpandAnim()
        }, 500L)
    }

    private var downX = 0f

    @SuppressLint("ClickableViewAccessibility")
    private fun handleLastPage() {
        views[0].canAutoClosed = false
        val view = views[views.size - 1]
        view.setOnTouchListener { _, event ->
            if (!view.isExpanded) {
                return@setOnTouchListener false
            }
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val res = when {
                        (event.x - downX).absoluteValue < 10f -> { //点击
                            onStart?.let {
                                it()
                            }
                            view.isExpanded
                        }
                        event.x - downX >= 10f -> {
                            false
                        }
                        else -> {
                            view.isExpanded
                        }
                    }
                    res
                }
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    false
                }
                else -> view.isExpanded
            }
        }
    }

    private fun showNext() {
        if (curShowPosition + 1 < views.size) {
            curShowPosition += 1
            if (curShowPosition + 1 < views.size) {
                views[curShowPosition + 1].showWithAnim()
            }
        }
    }

    private fun hideNext() {
        if (curShowPosition == 0) return
        if (curShowPosition + 1 < views.size) {
            if (views[curShowPosition + 1].visibility == View.VISIBLE) {
                views[curShowPosition + 1].hideWithAnim()
            }
        }
        curShowPosition -= 1
    }
}