package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func2(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(offset: Float): Float {
        return if (offset <= inParamMax / 2) {
            initValue + 2 * offset
        } else {
            initValue + inParamMax
        }
    }
}