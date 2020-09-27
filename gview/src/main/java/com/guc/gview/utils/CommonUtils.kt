package com.guc.gview.utils

import android.content.Context

/**
 * Created by guc on 2020/9/27.
 * Description：常用工具
 */
object CommonUtils {
    /**
     * dp => px
     */
    fun dp2px(context: Context, dp: Int): Int = dp2px(context, dp.toFloat())

    /**
     * dp => px
     */
    fun dp2px(context: Context, dp: Float): Int {
        return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    /**
     * px => dp
     */
    fun px2dp(context: Context, px: Int): Int = px2dp(context, px.toFloat())

    /**
     * px => dp
     */
    fun px2dp(context: Context, px: Float): Int {
        return (px / context.resources.displayMetrics.density + 0.5f).toInt()
    }
}