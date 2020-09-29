package com.guc.gview.textview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Created by guc on 2020/7/13.
 * Description：带自增动画效果的数字展示TextView
 */
class RiseNumberTextView(context: Context, attr: AttributeSet?, defStyle: Int) :
    AppCompatTextView(context, attr, defStyle) {
    constructor(context: Context, attr: AttributeSet) : this(context, attr, 0)
    constructor(context: Context) : this(context, null, 0)

    private lateinit var valueAnimator: ValueAnimator

    companion object {
        private const val TYPE_INT = 1
        private const val TYPE_FLOAT = 2
        private const val RUNNING = 1
        private const val STOPPED = 0
        private const val DEFAULT_DURATION = 1000L
    }

    private var playState = STOPPED
    private var numberType: Int = TYPE_FLOAT

    /**
     * 是否带分隔符
     */
    private var flags = true

    private var number = 0f
    private var fromNumber = 0f
    private var duration: Long = DEFAULT_DURATION
    var endListener: (() -> Unit)? = null
    private var dfs: DecimalFormat? = null


    fun start() {
        if (isRunning()) {
            if (::valueAnimator.isInitialized) {
                valueAnimator.removeAllUpdateListeners()
                valueAnimator.cancel()
            }
            playState = STOPPED
        }
        if (!isRunning()) {
            playState = RUNNING
            if (numberType == 1) runInt() else runFloat()
        }
    }

    fun withNumber(number: Float, flag: Boolean = true): RiseNumberTextView {
        this.number = number
        flags = flag
        numberType = TYPE_FLOAT
        fromNumber = 0f
        return this
    }

    fun withNumber(number: Int, flag: Boolean = true): RiseNumberTextView {
        this.number = number.toFloat()
        this.flags = flag
        numberType = TYPE_INT
        fromNumber = 0f
        return this
    }

    fun setDuration(duration: Long): RiseNumberTextView {
        this.duration = duration
        return this
    }

    private fun isRunning(): Boolean = playState == RUNNING

    private fun runFloat() {
        valueAnimator = ValueAnimator.ofFloat(fromNumber, number)
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener { valueAnimator ->
            if (flags) {
                text = (
                        format(",##0.00")
                            .format(valueAnimator.animatedValue.toString().toDouble())
                            .toString() + ""
                        )
                if (valueAnimator.animatedValue.toString()
                        .equals(number.toString() + "", ignoreCase = true)
                ) {
                    text = (format(",##0.00").format((number.toString() + "").toDouble()))
                }
            } else {
                text = (
                        format("##0.00")
                            .format(valueAnimator.animatedValue.toString().toDouble())
                            .toString() + ""
                        )
                if (valueAnimator.animatedValue.toString()
                        .equals(number.toString() + "", ignoreCase = true)
                ) {
                    text = (format("##0.00").format((number.toString() + "").toDouble()))
                }
            }
            if (valueAnimator.animatedFraction >= 1) {
                playState = STOPPED
                endListener?.let {
                    it()
                }
            }
        }
        valueAnimator.start()
    }

    private fun runInt() {
        valueAnimator = ValueAnimator.ofInt(fromNumber.toInt(), number.toInt())
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener { valueAnimator ->
            if (flags) {
                text =
                    format(",###").format(valueAnimator.animatedValue.toString().toInt()).toString()
                if (valueAnimator.animatedValue.toString()
                        .equals(number.toString() + "", ignoreCase = true)
                ) {
                    text = (format(",###").format((number.toString() + "").toDouble()))
                }
            } else {
                text = valueAnimator.animatedValue.toString()
            }
            if (valueAnimator.animatedFraction >= 1) {
                playState = STOPPED
                endListener?.let {
                    it()
                }
            }
        }
        valueAnimator.start()
    }

    private fun format(pattern: String): DecimalFormat {
        if (dfs == null) dfs = DecimalFormat()
        dfs!!.roundingMode = RoundingMode.FLOOR
        dfs!!.applyPattern(pattern)
        return dfs!!
    }

}