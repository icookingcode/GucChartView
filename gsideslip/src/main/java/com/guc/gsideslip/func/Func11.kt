package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func11(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(inParam: Float): Float {
        super.execute(inParam)
        return if (inParam < inParamMax / 2) {
            val k = (outParamMax - outParamMin) / (inParamMax / 2)
            outParamMin + inParam * k
        } else {
            outParamMax
        }
    }
}