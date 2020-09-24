package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
open class BaseFuncImpl(override var initValue: Float, override var inParamMax: Float) : IFunc {
    override var inParamMin: Float = 0F
    override var outParamMax: Float = 0F
    override var outParamMin: Float = 0F

    override fun execute(inParam: Float): Float = 0f
    override fun toString(): String {
        return "BaseFuncImpl(initValue=$initValue, inParamMax=$inParamMax, inParamMin=$inParamMin)"
    }
}