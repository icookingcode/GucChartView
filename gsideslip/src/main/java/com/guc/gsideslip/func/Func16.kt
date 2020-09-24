package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func16(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    override fun execute(offset: Float): Float {
        super.execute(offset)
        return if (offset > inParamMin && offset < inParamMax) {
            initValue + offset
        } else if (offset < inParamMin) {
            initValue
        } else {
            outParamMax
        }
    }
}