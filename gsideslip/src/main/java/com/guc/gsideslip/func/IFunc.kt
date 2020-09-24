package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
interface IFunc {
    /**
     * 初始值
     */
    var initValue: Float

    /**
     * 入参的阈值
     */
    var inParamMax: Float

    /**
     * 入参的阈值
     */
    var inParamMin: Float

    /**
     * 出参的阈值
     */
    var outParamMax: Float

    /**
     * 出参的阈值
     */
    var outParamMin: Float

    fun execute(inParam: Float): Float
}