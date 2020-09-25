package com.guc.gview.textview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.guc.gview.R
import java.lang.reflect.Field


/**
 * Created by guc on 2020/9/25.
 * Description：可折叠/展开显示过长文字的TextView
 */
class ExpandableTextView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    AppCompatTextView(context, attrs, defStyle) {
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.textViewStyle
    )

    constructor(context: Context) : this(context, null)

    companion object {
        const val STATE_SHRINK = 0
        const val STATE_EXPAND = 1

        private const val CLASS_NAME_VIEW = "android.view.View"
        private const val CLASS_NAME_LISTENER_INFO = "android.view.View\$ListenerInfo"
        private const val ELLIPSIS_HINT = "..."
        private const val GAP_TO_EXPAND_HINT = " "
        private const val GAP_TO_SHRINK_HINT = " "
        private const val MAX_LINES_ON_SHRINK = 2
        private const val TO_EXPAND_HINT_COLOR = -0xcb6725
        private const val TO_SHRINK_HINT_COLOR = -0x18b3c4
        private const val TO_EXPAND_HINT_COLOR_BG_PRESSED = 0x55999999
        private const val TO_SHRINK_HINT_COLOR_BG_PRESSED = 0x55999999
        private const val TOGGLE_ENABLE = false
        private const val SHOW_TO_EXPAND_HINT = true
        private const val SHOW_TO_SHRINK_HINT = true
    }

    private var mEllipsisHint: String? = null
    private var mToExpandHint: String? = null
    private var mToShrinkHint: String? = null
    private var mGapToExpandHint = GAP_TO_EXPAND_HINT
    private var mGapToShrinkHint = GAP_TO_SHRINK_HINT
    private var mToggleEnable = TOGGLE_ENABLE
    private var mShowToExpandHint = SHOW_TO_EXPAND_HINT
    private var mShowToShrinkHint = SHOW_TO_SHRINK_HINT
    private var mMaxLinesOnShrink = MAX_LINES_ON_SHRINK
    private var mToExpandHintColor = TO_EXPAND_HINT_COLOR
    private var mToShrinkHintColor = TO_SHRINK_HINT_COLOR
    private var mToExpandHintColorBgPressed = TO_EXPAND_HINT_COLOR_BG_PRESSED
    private var mToShrinkHintColorBgPressed = TO_SHRINK_HINT_COLOR_BG_PRESSED
    var currentState = STATE_SHRINK
        set(value) {
            field = value
            setCurrentState()
        }

    //  used to add to the tail of modified text, the "shrink" and "expand" text
    private var mTouchableSpan: TouchableSpan? = null
    private var mBufferType = BufferType.NORMAL
    private var mTextPaint: TextPaint? = null
    private var mLayout: Layout? = null
    private var mTextLineCount = -1
    private var mLayoutWidth = 0
    private var mFutureTextViewWidth = 0

    //  the original text of this view
    private var mOrigText: CharSequence? = null
    private var mExpandableClickListener: ExpandableClickListener? = null
    private var mOnExpandListener: OnExpandListener? = null

    init {
        initAttrs(context, attrs)
        init()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        mMaxLinesOnShrink = a.getInt(
            R.styleable.ExpandableTextView_etv_MaxLinesOnShrink,
            MAX_LINES_ON_SHRINK
        )
        mEllipsisHint = a.getString(R.styleable.ExpandableTextView_etv_EllipsisHint)
        mToExpandHint = a.getString(R.styleable.ExpandableTextView_etv_ToExpandHint)
        mToShrinkHint = a.getString(R.styleable.ExpandableTextView_etv_ToShrinkHint)
        mToggleEnable = a.getBoolean(R.styleable.ExpandableTextView_etv_EnableToggle, TOGGLE_ENABLE)
        mShowToExpandHint =
            a.getBoolean(R.styleable.ExpandableTextView_etv_IsShowExpandHint, SHOW_TO_EXPAND_HINT)
        mShowToShrinkHint =
            a.getBoolean(R.styleable.ExpandableTextView_etv_IsShowShrinkHint, SHOW_TO_SHRINK_HINT)
        mToExpandHintColor =
            a.getInteger(R.styleable.ExpandableTextView_etv_ToShrinkHintColor, TO_EXPAND_HINT_COLOR)
        mToShrinkHintColor =
            a.getInteger(R.styleable.ExpandableTextView_etv_ToShrinkHintColor, TO_SHRINK_HINT_COLOR)
        mToExpandHintColorBgPressed = a.getInteger(
            R.styleable.ExpandableTextView_etv_ToExpandHintColorBgPressed,
            TO_EXPAND_HINT_COLOR_BG_PRESSED
        )
        mToShrinkHintColorBgPressed = a.getInteger(
            R.styleable.ExpandableTextView_etv_ToShrinkHintColorBgPressed,
            TO_SHRINK_HINT_COLOR_BG_PRESSED
        )
        currentState = a.getInteger(R.styleable.ExpandableTextView_etv_InitState, STATE_SHRINK)
        mGapToExpandHint =
            a.getString(R.styleable.ExpandableTextView_etv_GapToExpandHint) ?: GAP_TO_EXPAND_HINT
        mGapToShrinkHint =
            a.getString(R.styleable.ExpandableTextView_etv_GapToShrinkHint) ?: GAP_TO_SHRINK_HINT
        a.recycle()
    }

    private fun init() {
        mTouchableSpan = TouchableSpan()
        movementMethod = LinkTouchMovementMethod()
        if (TextUtils.isEmpty(mEllipsisHint)) {
            mEllipsisHint = ELLIPSIS_HINT
        }
        if (TextUtils.isEmpty(mToExpandHint)) {
            mToExpandHint = resources.getString(R.string.to_expand_hint)
        }
        if (TextUtils.isEmpty(mToShrinkHint)) {
            mToShrinkHint = resources.getString(R.string.to_shrink_hint)
        }
        if (mToggleEnable) {
            mExpandableClickListener = ExpandableClickListener()
            setOnClickListener(mExpandableClickListener)
        }
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                setTextInternal(getNewTextByConfig(), mBufferType)
            }
        })
    }

    /**
     * used in ListView or RecyclerView to update ExpandableTextView
     *
     * @param text                original text
     * @param futureTextViewWidth the width of ExpandableTextView in px unit,
     * used to get max line number of original text by given the width
     * @param expandState         expand or shrink
     */
    fun updateForRecyclerView(
        text: CharSequence?,
        futureTextViewWidth: Int,
        expandState: Int
    ) {
        mFutureTextViewWidth = futureTextViewWidth
        currentState = expandState
        setText(text)
    }

    fun updateForRecyclerView(
        text: CharSequence?,
        type: BufferType?,
        futureTextViewWidth: Int
    ) {
        mFutureTextViewWidth = futureTextViewWidth
        setText(text!!, type!!)
    }

    fun updateForRecyclerView(
        text: CharSequence?,
        futureTextViewWidth: Int
    ) {
        mFutureTextViewWidth = futureTextViewWidth
        setText(text)
    }

    /**
     * get the current state of ExpandableTextView
     *
     * @return STATE_SHRINK if in shrink state
     * STATE_EXPAND if in expand state
     */
    fun getState(): Int {
        return currentState
    }

    /**
     * refresh and get a will-be-displayed text by current configuration
     *
     * @return get a will-be-displayed text
     */
    private fun getNewTextByConfig(): CharSequence? {
        if (TextUtils.isEmpty(mOrigText)) {
            return mOrigText
        }
        mLayout = layout
        if (mLayout != null) {
            mLayoutWidth = mLayout?.width ?: 0
        }
        if (mLayoutWidth <= 0) {
            mLayoutWidth = if (width == 0) {
                if (mFutureTextViewWidth == 0) {
                    return mOrigText
                } else {
                    mFutureTextViewWidth - paddingLeft - paddingRight
                }
            } else {
                width - paddingLeft - paddingRight
            }
        }
        mTextPaint = paint
        mTextLineCount = -1
        when (currentState) {
            STATE_SHRINK -> {
                mLayout = DynamicLayout(
                    mOrigText!!,
                    mTextPaint!!,
                    mLayoutWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
                )
                mTextLineCount = mLayout!!.lineCount
                if (mTextLineCount <= mMaxLinesOnShrink) {
                    return mOrigText
                }
                val indexEnd = getValidLayout()?.getLineEnd(mMaxLinesOnShrink - 1) ?: 0
                val indexStart = getValidLayout()?.getLineStart(mMaxLinesOnShrink - 1) ?: 0
                var indexEndTrimmed = (indexEnd
                        - getLengthOfString(mEllipsisHint)
                        - if (mShowToExpandHint) getLengthOfString(mToExpandHint) + getLengthOfString(
                    mGapToExpandHint
                ) else 0)
                if (indexEndTrimmed <= indexStart) {
                    indexEndTrimmed = indexEnd
                }
                val remainWidth = getValidLayout()?.width ?: 0 -
                (mTextPaint!!.measureText(
                    mOrigText!!.subSequence(indexStart, indexEndTrimmed).toString()
                ) + 0.5).toInt()
                val widthTailReplaced = mTextPaint!!.measureText(
                    getContentOfString(mEllipsisHint)
                        .toString() + if (mShowToExpandHint) getContentOfString(mToExpandHint) + getContentOfString(
                        mGapToExpandHint
                    ) else ""
                )
                var indexEndTrimmedRevised = indexEndTrimmed
                if (remainWidth > widthTailReplaced) {
                    var extraOffset = 0
                    var extraWidth = 0
                    while (remainWidth > widthTailReplaced + extraWidth) {
                        extraOffset++
                        extraWidth = if (indexEndTrimmed + extraOffset <= mOrigText!!.length) {
                            (mTextPaint!!.measureText(
                                mOrigText!!.subSequence(
                                    indexEndTrimmed,
                                    indexEndTrimmed + extraOffset
                                ).toString()
                            ) + 0.5).toInt()
                        } else {
                            break
                        }
                    }
                    indexEndTrimmedRevised += extraOffset - 1
                } else {
                    var extraOffset = 0
                    var extraWidth = 0
                    while (remainWidth + extraWidth < widthTailReplaced) {
                        extraOffset--
                        extraWidth = if (indexEndTrimmed + extraOffset > indexStart) {
                            (mTextPaint!!.measureText(
                                mOrigText!!.subSequence(
                                    indexEndTrimmed + extraOffset,
                                    indexEndTrimmed
                                ).toString()
                            ) + 0.5).toInt()
                        } else {
                            break
                        }
                    }
                    indexEndTrimmedRevised += extraOffset
                }
                val fixText =
                    removeEndLineBreak(mOrigText!!.subSequence(0, indexEndTrimmedRevised))
                val ssbShrink = SpannableStringBuilder(fixText)
                    .append(mEllipsisHint)
                if (mShowToExpandHint) {
                    ssbShrink.append(
                        getContentOfString(mGapToExpandHint) + getContentOfString(
                            mToExpandHint
                        )
                    )
                    ssbShrink.setSpan(
                        mTouchableSpan,
                        ssbShrink.length - getLengthOfString(mToExpandHint),
                        ssbShrink.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                return ssbShrink
            }
            STATE_EXPAND -> {
                if (!mShowToShrinkHint) {
                    return mOrigText
                }
                mLayout = DynamicLayout(
                    mOrigText!!,
                    mTextPaint!!,
                    mLayoutWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
                )
                mTextLineCount = mLayout!!.lineCount
                if (mTextLineCount <= mMaxLinesOnShrink) {
                    return mOrigText
                }
                val ssbExpand = SpannableStringBuilder(mOrigText)
                    .append(mGapToShrinkHint).append(mToShrinkHint)
                ssbExpand.setSpan(
                    mTouchableSpan,
                    ssbExpand.length - getLengthOfString(mToShrinkHint),
                    ssbExpand.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                return ssbExpand
            }
        }
        return mOrigText
    }

    private fun removeEndLineBreak(text: CharSequence): String {
        var str = text.toString()
        while (str.endsWith("\n")) {
            str = str.substring(0, str.length - 1)
        }
        return str
    }

    fun setExpandListener(listener: OnExpandListener) {
        mOnExpandListener = listener
    }

    private fun getValidLayout(): Layout? {
        return if (mLayout != null) mLayout else layout
    }

    override fun setText(text: CharSequence, type: BufferType) {
        mOrigText = text
        mBufferType = type
        setTextInternal(getNewTextByConfig(), type)
    }

    private fun setTextInternal(text: CharSequence?, type: BufferType) {
        super.setText(text, type)
    }

    private fun getLengthOfString(string: String?): Int {
        return string?.length ?: 0
    }

    private fun getContentOfString(string: String?): String? {
        return string ?: ""
    }

    @SuppressLint("ObsoleteSdkInt")
    fun getOnClickListener(view: View?): OnClickListener? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getOnClickListenerV14(view!!)
        } else {
            getOnClickListenerV(view!!)
        }
    }

    private fun getOnClickListenerV(view: View): OnClickListener? {
        var retrievedListener: OnClickListener? = null
        try {
            val field =
                Class.forName(CLASS_NAME_VIEW).getDeclaredField("mOnClickListener")
            field.isAccessible = true
            retrievedListener = field[view] as OnClickListener
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return retrievedListener
    }

    @SuppressLint("PrivateApi")
    private fun getOnClickListenerV14(view: View): OnClickListener? {
        var retrievedListener: OnClickListener? = null
        try {
            val listenerField: Field? =
                Class.forName(CLASS_NAME_VIEW).getDeclaredField("mListenerInfo")
            var listenerInfo: Any? = null
            if (listenerField != null) {
                listenerField.isAccessible = true
                listenerInfo = listenerField.get(view)
            }
            val clickListenerField: Field? = Class.forName(CLASS_NAME_LISTENER_INFO)
                .getDeclaredField("mOnClickListener")
            if (clickListenerField != null && listenerInfo != null) {
                clickListenerField.isAccessible = true
                retrievedListener =
                    clickListenerField.get(listenerInfo) as OnClickListener
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retrievedListener
    }

    private fun setCurrentState() {
        when (currentState) {
            STATE_SHRINK -> {
                if (mOnExpandListener != null) {
                    mOnExpandListener?.onShrink(this)
                }
            }
            STATE_EXPAND -> {
                if (mOnExpandListener != null) {
                    mOnExpandListener?.onExpand(this)
                }
            }
        }
        setTextInternal(getNewTextByConfig(), mBufferType)
    }

    private fun toggle() {
        when (currentState) {
            STATE_SHRINK -> {
                currentState = STATE_EXPAND
                if (mOnExpandListener != null) {
                    mOnExpandListener?.onExpand(this)
                }
            }
            STATE_EXPAND -> {
                currentState = STATE_SHRINK
                if (mOnExpandListener != null) {
                    mOnExpandListener?.onShrink(this)
                }
            }
        }
        setTextInternal(getNewTextByConfig(), mBufferType)
    }

    interface OnExpandListener {
        fun onExpand(view: ExpandableTextView)
        fun onShrink(view: ExpandableTextView)
    }

    inner class ExpandableClickListener : OnClickListener {
        override fun onClick(view: View) {
            toggle()
        }
    }

    inner class TouchableSpan : ClickableSpan() {
        private var mIsPressed = false
        fun setPressed(isSelected: Boolean) {
            mIsPressed = isSelected
        }

        override fun onClick(widget: View) {
            if (hasOnClickListeners()
                && getOnClickListener(this@ExpandableTextView) is ExpandableClickListener
            ) {
                Log.i("ExpandableTextView", "has set ExpandableClickListener")
            } else {
                toggle()
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            when (currentState) {
                STATE_SHRINK -> {
                    ds.color = mToExpandHintColor
                    ds.bgColor = if (mIsPressed) mToExpandHintColorBgPressed else 0
                }
                STATE_EXPAND -> {
                    ds.color = mToShrinkHintColor
                    ds.bgColor = if (mIsPressed) mToShrinkHintColorBgPressed else 0
                }
            }
            ds.isUnderlineText = false
        }
    }

    class LinkTouchMovementMethod : LinkMovementMethod() {
        private var mPressedSpan: TouchableSpan? = null
        override fun onTouchEvent(
            textView: TextView,
            spannable: Spannable,
            event: MotionEvent
        ): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event)
                if (mPressedSpan != null) {
                    mPressedSpan?.setPressed(true)
                    Selection.setSelection(
                        spannable, spannable.getSpanStart(mPressedSpan),
                        spannable.getSpanEnd(mPressedSpan)
                    )
                }
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                val touchedSpan: TouchableSpan? = getPressedSpan(textView, spannable, event)
                if (mPressedSpan != null && touchedSpan !== mPressedSpan) {
                    mPressedSpan?.setPressed(false)
                    mPressedSpan = null
                    Selection.removeSelection(spannable)
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan?.setPressed(false)
                    super.onTouchEvent(textView, spannable, event)
                }
                mPressedSpan = null
                Selection.removeSelection(spannable)
            }
            return true
        }

        private fun getPressedSpan(
            textView: TextView,
            spannable: Spannable,
            event: MotionEvent
        ): TouchableSpan? {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= textView.totalPaddingLeft
            y -= textView.totalPaddingTop
            x += textView.scrollX
            y += textView.scrollY
            val layout = textView.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link: Array<TouchableSpan> = spannable.getSpans<TouchableSpan>(
                off, off,
                TouchableSpan::class.java
            )
            var touchedSpan: TouchableSpan? = null
            if (link.isNotEmpty()) {
                touchedSpan = link[0]
            }
            return touchedSpan
        }
    }
}