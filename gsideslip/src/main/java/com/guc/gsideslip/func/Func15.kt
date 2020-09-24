package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func15(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {

    var rate: Float = 0F

    override fun execute(inParam: Float): Float {
        super.execute(inParam)
        return if (inParam < (inParamMax - inParamMin) / 4) {
            outParamMin + inParam
        } else if (inParam > (inParamMax - inParamMin) / 4 && inParam <= inParamMax) {
            val result = inParam * rate + outParamMin
            if (result >= outParamMax) {
                outParamMax
            } else {
                result
            }
        } else {
            outParamMax
        }
    }
}