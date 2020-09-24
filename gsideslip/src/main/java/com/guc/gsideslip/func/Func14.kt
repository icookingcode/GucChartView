package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func14(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(inParam: Float): Float {
        super.execute(inParam)
        return if (inParam < inParamMax / 2) {
            outParamMax
        } else if (inParam >= (inParamMax - inParamMin) / 2 && inParam < inParamMax) {
            val k = (outParamMin - outParamMax) / ((inParamMax - inParamMin) / 2)
            outParamMax + (inParam - (inParamMax - inParamMin) / 2) * k
        } else {
            outParamMin
        }
    }
}