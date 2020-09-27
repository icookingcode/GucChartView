package com.guc.gview.loading

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * Created by Guc on 2020/9/25.
 * Description：线条加载动画
 */
class LineLoadingDrawable : Drawable() {

    private var transAnimator1: ValueAnimator? = null
    private var transAnimator2: ValueAnimator? = null
    private var mPaint: Paint = Paint()

    var offset1: Int = 0
    var offset2: Int = 0

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 10F
        mPaint.color = Color.LTGRAY
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }


    override fun draw(canvas: Canvas) {
        val path = Path()
        path.moveTo(
            bounds.left.toFloat() + mPaint.strokeWidth,
            bounds.bottom.toFloat() - mPaint.strokeWidth - offset1
        )
        path.lineTo(
            bounds.right.toFloat() - mPaint.strokeWidth,
            bounds.bottom.toFloat() - mPaint.strokeWidth - offset2 - bounds.height() / 10
        )
        path.lineTo(
            bounds.right.toFloat() - mPaint.strokeWidth,
            bounds.top.toFloat() + mPaint.strokeWidth + offset2
        )
        path.lineTo(
            bounds.left.toFloat() + mPaint.strokeWidth,
            bounds.top.toFloat() + mPaint.strokeWidth + offset1 + bounds.height() / 10
        )
        path.close()
        canvas.drawPath(path, mPaint)
    }

    fun start(): LineLoadingDrawable {
        transAnimator1?.cancel()
        transAnimator2?.cancel()
        transAnimator1 = ValueAnimator.ofInt(0, 0, bounds.height() * 9 / 10, 0)?.apply {
            duration = 1000L
            addUpdateListener {
                offset1 = it.animatedValue as Int
                invalidateSelf()
            }
            repeatCount = -1
            start()
        }

        transAnimator2 = ValueAnimator.ofInt(0, bounds.height() * 9 / 10, 0, 0).apply {
            duration = 1000L
            addUpdateListener {
                offset2 = it.animatedValue as Int
                invalidateSelf()
            }
            repeatCount = -1
            start()
        }
        return this
    }

}