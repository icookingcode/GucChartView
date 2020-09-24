package com.guc.gsideslip.utils

import android.graphics.Bitmap

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
object Utils {
    fun getTransparentBitmap(sourceImg: Bitmap, number: Int): Bitmap {
        var sourceImg = sourceImg
        var number = number
        val argb = IntArray(sourceImg.width * sourceImg.height)

        sourceImg.getPixels(argb, 0, sourceImg.width, 0, 0, sourceImg.width, sourceImg.height)

        number = number * 255 / 100

        for (i in argb.indices) {
            argb[i] = number shl 24 or (argb[i] and 0x00FFFFFF)
        }

        sourceImg =
            Bitmap.createBitmap(argb, sourceImg.width, sourceImg.height, Bitmap.Config.ARGB_8888)
        return sourceImg
    }
}