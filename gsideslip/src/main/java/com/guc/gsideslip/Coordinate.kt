package com.guc.gsideslip

import com.guc.gsideslip.func.IFunc

/**
 * Created by guc on 2020/9/23.
 * Description：坐标类
 */
open class Coordinate {
    var x: Float = 0F
    var y: Float = 0F
    var xFunc: IFunc? = null
    var yFunc: IFunc? = null
    override fun toString(): String {
        return "Coordinate(x=$x, y=$y, xFunc=$xFunc, yFunc=$yFunc)"
    }
}