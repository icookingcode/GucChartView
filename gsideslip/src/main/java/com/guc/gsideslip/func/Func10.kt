package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func10(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(inParam: Float): Float {
        super.execute(inParam)
        return if (inParam < inParamMax / 4) {
            outParamMin
        } else if (inParam >= inParamMax / 4 && inParam < inParamMax * 3 / 4) {
            val k = (outParamMax - outParamMin) / (inParamMax / 2)
            outParamMin + (inParam - inParamMax / 4) * k
        } else {
            outParamMax
        }
    }
}