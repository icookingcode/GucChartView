package com.guc.gsideslip

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import androidx.annotation.ColorInt
import com.guc.gsideslip.func.*

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class SideSlipView : View {
    companion object {
        val TAG = SideSlipView::class.simpleName
    }

    /**
     * 浪高
     */
    private var waveHeight: Float = 0F

    /**
     * 初始浪高
     */
    private var oriWaveHeight: Float = 0F

    /**
     * 构成波浪的关键点坐标
     */
    private var pointA: Coordinate = Coordinate()
    private var pointB: Coordinate = Coordinate()
    private var pointC: Coordinate = Coordinate()
    private var pointD: Coordinate = Coordinate()
    private var pointE: Coordinate = Coordinate()
    private var pointF: Coordinate = Coordinate()
    private var pointG: Coordinate = Coordinate()

    @ColorInt
    var color: Int = Color.RED

    /**
     * 是否为展开状态
     */
    var isExpanded: Boolean = false
    var canAutoClosed: Boolean = true

    /**
     * 状态
     */
    private var state: Int = STATE_SHRINKED

    /**
     * 波浪基线高度
     */
    private var waveLineHeight: Float = 0F
    private var path: Path = Path()

    private var paint: Paint = Paint()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    private fun initView(context: Context?, attrs: AttributeSet?) {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawWave(canvas)
        drawSrcBm(canvas)
        drawIndicator(canvas)

    }

    private fun drawSrcBm(canvas: Canvas?) {
        canvas?.let {
            if (srcBm == null) {
                return
            }
            clipSrcBm()
            it.drawBitmap(tempBm!!, 0F, 0F, null)
        }
    }

    private var tempCanvas: Canvas? = null
    private var tempBm: Bitmap? = null

    private fun clipSrcBm() {
        paint.xfermode = null
        if (tempBm == null) {
            tempBm = Bitmap.createBitmap(srcBm?.width!!, srcBm?.height!!, Bitmap.Config.ARGB_8888)
        }
        if (tempCanvas == null) {
            tempCanvas = Canvas(tempBm!!)
        }
        tempCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        tempCanvas?.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        tempCanvas?.drawBitmap(
            srcBm!!,
            Rect(0, 0, srcBm?.width!!, srcBm?.height!!),
            Rect(0, 0, width, height),
            paint
        )
    }

    private var backBm: Bitmap? = null
    private var isNeedDrawBackBm: Boolean = true

    private fun drawIndicator(canvas: Canvas?) {
        if (!isNeedDrawBackBm) {
            return
        }
        canvas?.apply {
            if (backBm == null) {
                backBm = BitmapFactory.decodeResource(resources, R.drawable.img_back)
                backBm?.setHasAlpha(true)
            }
            val backBmCenterX: Int = (width - oriWaveHeight / 2).toInt()
            val backBmCenterY: Int = height / 2
            this.drawBitmap(
                backBm!!,
                Rect(0, 0, backBm!!.width, backBm!!.height),
                Rect(
                    backBmCenterX - (oriWaveHeight / 8).toInt(),
                    backBmCenterY - (oriWaveHeight / 8).toInt(),
                    backBmCenterX + (oriWaveHeight / 8).toInt(),
                    backBmCenterY + (oriWaveHeight / 8).toInt()
                ),
                null
            )
        }
    }

    private fun drawWave(canvas: Canvas?) {
        canvas?.let {
            configPath()
            it.drawFilter = PaintFlagsDrawFilter(0, FILTER_BITMAP_FLAG or ANTI_ALIAS_FLAG)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        oriWaveHeight = width / 6.toFloat()

        waveHeight = oriWaveHeight

        waveLineHeight = h / 2F

        pointA.x = width - oriWaveHeight / 4
        pointA.y = waveLineHeight - oriWaveHeight


        pointB.x = width - oriWaveHeight / 4
        pointB.y = waveLineHeight - 3 * oriWaveHeight / 4



        pointC.x = width - oriWaveHeight / 2
        pointC.y = waveLineHeight - oriWaveHeight / 2


        pointD = getPointDCoordinate(pointB, pointC)

        pointE.x = width - oriWaveHeight / 2
        pointE.y = waveLineHeight + oriWaveHeight / 2


        pointF.x = width - oriWaveHeight / 4
        pointF.y = waveLineHeight + 3 * oriWaveHeight / 4


        pointG.x = width - oriWaveHeight / 4
        pointG.y = waveLineHeight + oriWaveHeight

        //init path
        configPath()

        configExpandFunc()
    }

    private fun configPath(): Path {

        path.reset()
        path.moveTo(width.toFloat(), 0F)
        path.lineTo(pointA.x, 0F)
        path.lineTo(pointA.x, pointA.y)

        path.quadTo(pointB.x, pointB.y, pointC.x, pointC.y)
        path.quadTo(pointD.x, pointD.y, pointE.x, pointE.y)
        path.quadTo(pointF.x, pointF.y, pointG.x, pointG.y)

        path.lineTo(pointG.x, pointG.y)
        path.lineTo(pointG.x, height.toFloat())
        path.lineTo(width.toFloat(), height.toFloat())
        path.close()

        return path
    }

    fun getPointDCoordinate(pointB: Coordinate, pointC: Coordinate): Coordinate {
        val dy = pointC.y - pointB.y
        val dx = pointB.x - pointC.x
        //B点到D点的距离
        val tempDy = waveLineHeight - pointB.y
        pointD.x = pointB.x - (dx * tempDy / dy)
        pointD.y = waveLineHeight
        return pointD
    }

    private fun executePointFunc(point: Coordinate, offset: Float) {
        point.xFunc?.let {
            point.x = it.execute(offset)
        }
        point.yFunc?.let {
            point.y = it.execute(offset)
        }
    }

    private fun resetInitValueFunc(point: Coordinate) {
        point.xFunc?.let {
            it.initValue = point.x
        }

        point.yFunc?.let {
            it.initValue = point.y
        }
    }

    private var offsetAnimator: ValueAnimator? = null

    /**
     * 开启展开动画
     */
    fun startExpandAnim() {
        if (state == STATE_MOVING) return
        state = STATE_MOVING
        isNeedDrawBackBm = false
        offsetAnimator?.cancel()
        offsetAnimator = ValueAnimator.ofFloat(offsetX, -width.toFloat())
        offsetAnimator?.let {
            it.duration = 800L
            it.interpolator = BounceInterpolator()
            it.addUpdateListener {
                val tempOffsetX: Float = it.animatedValue as Float
                executePointFunc(pointA, tempOffsetX)
                executePointFunc(pointB, tempOffsetX)
                executePointFunc(pointC, tempOffsetX)
                getPointDCoordinate(pointB, pointC)
                executePointFunc(pointE, tempOffsetX)
                executePointFunc(pointF, tempOffsetX)
                executePointFunc(pointG, tempOffsetX)

                postInvalidate()
            }

            it.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    //重新设置变换函数
                    configShrinkFunc()

                    resetInitValueFunc(pointA)
                    resetInitValueFunc(pointB)
                    resetInitValueFunc(pointC)
                    getPointDCoordinate(pointB, pointC)
                    resetInitValueFunc(pointE)
                    resetInitValueFunc(pointF)
                    resetInitValueFunc(pointG)
                    isExpanded = true
                    state = STATE_EXPANDED
                    listener?.onStateChanged(STATE_EXPANDED)
                }
            })
            it.start()
        }
        listener?.onStateChanged(STATE_MOVING)
    }

    /**
     * 开启关闭动画
     */
    private fun startShrinkAnim() {
        if (state == STATE_MOVING) return
        state = STATE_MOVING
        offsetAnimator?.cancel()
        offsetAnimator = ValueAnimator.ofFloat(offsetX, width.toFloat())
        offsetAnimator?.let {
            it.duration = 800L
            it.interpolator = AccelerateDecelerateInterpolator()
            it.addUpdateListener {
                val tempOffsetX: Float = it.animatedValue as Float
                executePointFunc(pointA, tempOffsetX)
                executePointFunc(pointB, tempOffsetX)
                executePointFunc(pointC, tempOffsetX)
                getPointDCoordinate(pointB, pointC)
                executePointFunc(pointE, tempOffsetX)
                executePointFunc(pointF, tempOffsetX)
                executePointFunc(pointG, tempOffsetX)

                postInvalidate()
            }

            it.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    isNeedDrawBackBm = true

                    //重新设置变换函数
                    configExpandFunc()

                    resetInitValueFunc(pointA)
                    resetInitValueFunc(pointB)
                    resetInitValueFunc(pointC)
                    getPointDCoordinate(pointB, pointC)
                    resetInitValueFunc(pointE)
                    resetInitValueFunc(pointF)
                    resetInitValueFunc(pointG)
                    isExpanded = false
                    state = STATE_SHRINKED
                    listener?.onStateChanged(STATE_SHRINKED)
                }
            })
            it.start()
        }
        listener?.onStateChanged(STATE_MOVING)
    }

    fun configExpandFunc() {
        pointA.xFunc = Func5(pointA.x, pointA.x)
        val pointAyFunc = Func7(pointA.y, pointA.y)
        pointAyFunc.rate = 3 * width / height.toFloat()
        pointA.yFunc = pointAyFunc

        pointB.xFunc = Func5(pointB.x, pointB.x)
        val pointByFunc = Func7(pointB.y, pointB.y)
        pointByFunc.rate = 2 * width / height.toFloat()
        pointB.yFunc = pointByFunc

        pointC.xFunc = Func5(pointC.x, pointC.x)
        val pointCyFunc = Func7(pointC.y, pointC.y)
        pointCyFunc.rate = width / height.toFloat()
        pointC.yFunc = pointCyFunc

        pointE.xFunc = Func5(pointE.x, pointE.x)
        val pointEyFunc = Func8(pointE.y, height.toFloat())
        pointEyFunc.rate = width / height.toFloat()
//        pointEyFunc.rate = 2 * ((height - pointE.y) / (height - pointE.y - oriWaveHeight / 4)) * width / height.toFloat()
        pointEyFunc.inParamMin = pointE.y
        pointE.yFunc = pointEyFunc

        pointF.xFunc = Func5(pointF.x, pointF.x)
        val pointFyFunc = Func8(pointF.y, height.toFloat())
        pointFyFunc.rate = 2 * width / height.toFloat()
        pointFyFunc.inParamMin = pointF.y
        pointF.yFunc = pointFyFunc

        pointG.xFunc = Func5(pointG.x, pointG.x)
        val pointGyFunc = Func8(pointG.y, height.toFloat())
        pointGyFunc.rate = 3 * width / height.toFloat()
        pointGyFunc.inParamMin = pointG.y
        pointG.yFunc = pointGyFunc
    }

    fun configShrinkFunc() {

        val pointAyFunc = Func9(0F, width.toFloat())
        pointAyFunc.inParamMax = width.toFloat()
        pointAyFunc.inParamMin = 0F
        pointAyFunc.outParamMax = waveLineHeight - oriWaveHeight
        pointAyFunc.outParamMin = 0F
        pointAyFunc.initValue = 0F
        pointA.yFunc = pointAyFunc

        val pointByFunc = Func10(0F, width.toFloat())
        pointByFunc.inParamMax = width.toFloat()
        pointByFunc.inParamMin = 0F
        pointByFunc.outParamMax = waveLineHeight - 3 * oriWaveHeight / 4
        pointByFunc.outParamMin = 0F
        pointByFunc.initValue = 0F
        pointB.yFunc = pointByFunc

        val pointCyFunc = Func11(pointC.y, width.toFloat())
        pointCyFunc.inParamMax = width.toFloat()
        pointCyFunc.inParamMin = 0F
        pointCyFunc.outParamMax = waveLineHeight - oriWaveHeight / 2
        pointCyFunc.outParamMin = pointC.y
        pointCyFunc.initValue = pointC.y
        pointC.yFunc = pointCyFunc


        val pointEyFunc = Func12(0F, width.toFloat())
        pointEyFunc.inParamMax = width.toFloat()
        pointEyFunc.inParamMin = 0F
        pointEyFunc.outParamMax = pointE.y
        pointEyFunc.outParamMin = waveLineHeight + oriWaveHeight / 2
        pointEyFunc.initValue = pointE.y
        pointE.yFunc = pointEyFunc

        val pointFyFunc = Func13(0F, width.toFloat())
        pointFyFunc.inParamMax = width.toFloat()
        pointFyFunc.inParamMin = 0F
        pointFyFunc.outParamMax = pointF.y
        pointFyFunc.outParamMin = waveLineHeight + 3 * oriWaveHeight / 4
        pointFyFunc.initValue = pointE.y
        pointF.yFunc = pointFyFunc

        val pointGyFunc = Func14(0F, width.toFloat())
        pointGyFunc.inParamMax = width.toFloat()
        pointGyFunc.inParamMin = 0F
        pointGyFunc.outParamMax = pointG.y
        pointGyFunc.outParamMin = waveLineHeight + oriWaveHeight
        pointGyFunc.initValue = pointE.y
        pointG.yFunc = pointGyFunc
    }

    private fun isInWavePathRegion(x: Float, y: Float): Boolean {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        val region = Region()
        region.setPath(
            path,
            Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        )
        if (region.contains(x.toInt(), y.toInt())) {
            return true
        }
        return false
    }

    private var downX: Float = 0F
    private var downY: Float = 0F
    private var offsetX: Float = 0F

    // 判断某次操作是否有效，用于将事件向下传递
    private var isEffectOperation: Boolean = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_UP -> {
                    if (!isEffectOperation || !canAutoClosed) {
                        return super.onTouchEvent(event)
                    }
                    if (!isExpanded) {
                        startExpandAnim()
                    } else {
                        startShrinkAnim()
                    }

                    downX = 0F
                    downY = 0F
                    offsetX = 0F
                    isEffectOperation = false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isEffectOperation || !canAutoClosed) {
                        return super.onTouchEvent(event)
                    }
                    isNeedDrawBackBm = false
                    offsetX = it.x - downX
                    executePointFunc(pointA, offsetX)
                    executePointFunc(pointB, offsetX)
                    executePointFunc(pointC, offsetX)
                    getPointDCoordinate(pointB, pointC)
                    executePointFunc(pointE, offsetX)
                    executePointFunc(pointF, offsetX)
                    executePointFunc(pointG, offsetX)

                    listener?.onStateChanged(STATE_MOVING)

                    postInvalidate()
                }
                MotionEvent.ACTION_DOWN -> {
                    downX = it.x
                    downY = it.y
                    if (isInWavePathRegion(downX, downY)) {
                        isEffectOperation = true
                        postInvalidate()
                    } else {
                        //向下传递
                        return super.onTouchEvent(event)
                    }
                }
            }

        }
        return true
    }

    var listener: OnStateChangedListener? = null

    interface OnStateChangedListener {
        fun onStateChanged(state: Int)
    }

    fun setOnStateChangedListener(listener: OnStateChangedListener) {
        this.listener = listener
    }

    fun showWithAnim() {
        animation?.cancel()
        val showAnim = TranslateAnimation(oriWaveHeight, 0F, 0F, 0F)
        showAnim.duration = 500L
        showAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        startAnimation(showAnim)
        visibility = View.VISIBLE
    }

    fun hideWithAnim() {
        animation?.cancel()
        translationX = 0F
        val hideAnim = TranslateAnimation(0F, oriWaveHeight, 0F, 0F)
        hideAnim.duration = 500L
        hideAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
                translationX = 0F
            }

        })
        startAnimation(hideAnim)
        visibility = View.VISIBLE
    }


    var srcBm: Bitmap? = null

    fun setImageResource(resId: Int) {
        srcBm = BitmapFactory.decodeResource(resources, resId)
        postInvalidate()
    }
}