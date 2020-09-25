package com.guc.gview.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by guc on 2020/9/24.
 * Description：LED效果的TextView
 */
class LedTextView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    AppCompatTextView(context, attrs, defStyle) {
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.textViewStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        this.typeface = Typeface.createFromAsset(context.assets, "fonts/digital-7.ttf")
    }
}