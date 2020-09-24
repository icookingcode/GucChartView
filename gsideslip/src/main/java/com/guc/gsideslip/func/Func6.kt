package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func6(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(offset: Float): Float {
        return when {
            initValue + offset > inParamMax -> {
                inParamMax
            }
            initValue + offset <= 0 -> {
                0F
            }
            else -> {
                initValue + offset * 2
            }
        }
    }
}