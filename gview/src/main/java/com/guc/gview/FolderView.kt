package com.guc.gview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.view_folder.view.*

/**
 * Created by guc on 2020/9/25.
 * Description：折叠工具
 */
class FolderView(
    ctx: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : FrameLayout(ctx, attrs, defStyleAttr), View.OnClickListener {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    var controlledView: View? = null
        set(value) {
            field = value
            changeView()
        }

    /**
     * true:展开  false:折叠
     */
    var folderType: Boolean = false
        set(value) {
            field = value
            initFolder()
        }
    private var draArrowUp: Drawable? = null
    private var draArrowDown: Drawable? = null
    var textColor: Int = 0
        set(value) {
            field = value
            tvContent.setTextColor(value)
        }
    var textSize: Int = 0
        set(value) {
            field = value
            tvContent.textSize = value.toFloat()
        }
    var isTextVisible: Boolean = true
        set(value) {
            field = value
            tvContent.visibility = if (value) View.VISIBLE else View.GONE
        }
    var tipFold = "展开"
        set(value) {
            field = value
            initFolder()
        }
    var tipUnfold = "折叠"
        set(value) {
            field = value
            initFolder()
        }

    init {
        View.inflate(context, R.layout.view_folder, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.FolderView)
        textColor = a.getColor(R.styleable.FolderView_android_textColor, Color.BLUE)
        textSize = (a.getDimensionPixelSize(
            R.styleable.FolderView_android_textSize, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12f,
                resources.displayMetrics
            ).toInt()
        ) / resources.displayMetrics.scaledDensity + 0.5
                ).toInt() //px -> sp
        isTextVisible = a.getBoolean(R.styleable.FolderView_isTextVisible, true)

        draArrowUp =
            a.getDrawable(R.styleable.FolderView_arrowUpDrawable) ?: ContextCompat.getDrawable(
                context,
                R.drawable.arrow_up
            )
        draArrowDown =
            a.getDrawable(R.styleable.FolderView_arrowDownDrawable) ?: ContextCompat.getDrawable(
                context,
                R.drawable.arrow_down
            )
        val controlled = a.getResourceId(R.styleable.FolderView_controlledView, -1)
        if (controlled != -1) {
            post {
                controlledView = rootView.findViewById(controlled)
            }
        }
        a.getText(R.styleable.FolderView_tipFold)?.let {
            tipFold = it.toString()
        }
        a.getText(R.styleable.FolderView_tipUnfold)?.let {
            tipUnfold = it.toString()
        }
        a.recycle()
        initView()
    }

    override fun onClick(v: View?) {
        folderType = !folderType
    }

    private fun initView() {
        tvContent.setTextColor(textColor)
        tvContent.textSize = textSize.toFloat()
        tvContent.visibility = if (isTextVisible) View.VISIBLE else View.GONE
        this.setOnClickListener(this)
        initFolder()
    }

    private fun initFolder() {
        if (folderType) {
            tvContent.text = tipUnfold
            ivArrow.setImageDrawable(draArrowUp)
        } else {
            tvContent.text = tipFold
            ivArrow.setImageDrawable(draArrowDown)
        }
        changeView()
    }

    private fun changeView() {
        controlledView?.visibility = if (folderType) View.VISIBLE else View.GONE
    }
}