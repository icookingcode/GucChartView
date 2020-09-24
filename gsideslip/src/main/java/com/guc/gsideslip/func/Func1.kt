package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func1(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(offset: Float): Float {
        return if (offset > inParamMax / 2 && offset <= inParamMax) {
            initValue + 2 * (offset - (inParamMax / 2))
        } else if (offset > inParamMax) {
            initValue + inParamMax
        } else {
            initValue
        }
    }
}