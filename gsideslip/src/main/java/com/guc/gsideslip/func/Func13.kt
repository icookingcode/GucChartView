package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func13(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(inParam: Float): Float {
        super.execute(inParam)
        return if (inParam < inParamMax / 4) {
            outParamMax
        } else if (inParam >= (inParamMax - inParamMax) / 4 && inParam < (inParamMax - inParamMin) * 3 / 4) {
            val k = (outParamMin - outParamMax) / (inParamMax / 2)
            outParamMax + (inParam - (inParamMax - inParamMin) / 4) * k
        } else {
            outParamMin
        }
    }
}