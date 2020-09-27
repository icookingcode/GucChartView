package com.guc.gview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.guc.gview.utils.CommonUtils
import java.io.File
import java.io.FileOutputStream


/**
 * Created by guc on 2020/9/27.
 * Description：签名控件
 */
class SignNameView(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    private val mPaint: Paint
    private val mPath: Path

    /**
     * 签名画笔
     */
    private lateinit var cacheCanvas: Canvas

    /**
     * 签名画布
     */
    private lateinit var cacheBitmap: Bitmap
    private var mBackColor = 0
    private var mLineColor = 0
    private var mLineWidth = 0
    private var hasSign = false

    init {
        initAttrs(attrs, defStyleAttr)
        mPaint = Paint()
        mPaint.color = mLineColor
        mPaint.strokeWidth = mLineWidth.toFloat()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPath = Path()
    }

    /**
     * 清空内容
     */
    fun clean() {
        mPath.reset()
        hasSign = false
        if (::cacheCanvas.isInitialized) cacheCanvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    /**
     * 获取签名bitmap
     */
    fun getSignBitmap() = cacheBitmap

    /**
     * 保存签名图片
     */
    fun saveSign(path: String) = save(path, false, 0)

    /**
     * 是否已签名
     */
    fun hasSign() = hasSign

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //创建跟view一样大的bitmap，用来保存签名
        cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas(cacheBitmap)
        cacheCanvas.drawColor(mBackColor)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> { //按下
                mPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> { //移动
                mPath.lineTo(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> { //抬起
                hasSign = true
                //将路径画到bitmap中，即一次笔画完成才去更新bitmap，而手势轨迹是实时显示在画板上的。
                cacheCanvas.drawPath(mPath, mPaint)
            }

        }
        //更新绘制
        invalidate()
        return true
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val array: TypedArray = mContext.theme.obtainStyledAttributes(
            attrs, R.styleable.SignNameView, defStyleAttr, 0
        )
        mLineWidth = array.getDimensionPixelSize(
            R.styleable.SignNameView_lineWith,
            CommonUtils.dp2px(context, 1)
        )
        mBackColor = array.getColor(R.styleable.SignNameView_bgColor, Color.WHITE)
        mLineColor = array.getColor(R.styleable.SignNameView_lineColor, Color.BLACK)
        array.recycle()
    }

    /**
     * 保存画板
     *
     * @param path       保存到路径
     * @param clearBlank 是否清除边缘空白区域
     * @param blank      要保留的边缘空白距离
     */
    private fun save(path: String, clearBlank: Boolean, blank: Int): Boolean {
        if (!::cacheBitmap.isInitialized) return false
        val bitmap = cacheBitmap
        try {
            val file = File(path)
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            }
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
        }

    }

}