package com.guc.gsideslip.func

/**
 * Created by guc on 2020/9/23.
 * Description：
 */
class Func7(initValue: Float, inParamMax: Float) : BaseFuncImpl(initValue, inParamMax) {
    var rate: Float = 0F
    override fun execute(inParam: Float): Float {
        super.execute(inParam)
        val offset = inParam * rate
        return if (initValue + offset > inParamMin && initValue + offset < inParamMax) {
            initValue + offset
        } else if (initValue + offset <= inParamMin) {
            inParamMin
        } else {
            inParamMax
        }
    }
}