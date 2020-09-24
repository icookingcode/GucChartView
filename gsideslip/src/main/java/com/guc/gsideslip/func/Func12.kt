package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func12(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(inParam: Float): Float {
        super.execute(inParam)
        return if (inParam < inParamMax / 2) {
            val k = (outParamMin - outParamMax) / ((inParamMax - inParamMin) / 2)
            outParamMax + inParam * k
        } else {
            outParamMin
        }
    }
}