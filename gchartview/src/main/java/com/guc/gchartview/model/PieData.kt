package com.guc.gchartview.model

import android.graphics.Color
import android.graphics.Point

/**
 * Created by guc on 2020/7/29.
 * Description：
 */
/**
 * 饼状图数据类
 */
class PieData(
    var score: Float,
    var describe: String?,
    var colorLine: Int,
    var colorDescribe: Int = Color.parseColor("#999999"),
    var colorScore: Int = Color.parseColor("#333333")
) : Comparable<PieData> {

    override fun compareTo(other: PieData): Int {
        return this.centerPoint.y - other.centerPoint.y
    }


    var proportion //占比
            = 0f
    lateinit var centerPoint //指示点位置
            : Point
    var isRight = true //右侧的点
}