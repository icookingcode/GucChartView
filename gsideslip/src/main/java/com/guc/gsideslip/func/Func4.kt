package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func4(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(offset: Float): Float {
        return when {
            offset < inParamMax / 2 -> {
                initValue
            }
            offset > inParamMax -> {
                initValue - inParamMax
            }
            else -> {
                initValue - 2 * (offset - (inParamMax / 2))
            }
        }
    }
}