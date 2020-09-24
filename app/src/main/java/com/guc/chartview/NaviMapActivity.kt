package com.guc.chartview

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol
import com.amap.api.location.AMapLocationQualityReport
import com.amap.api.maps.AMap
import com.amap.api.maps.model.MyLocationStyle
import kotlinx.android.synthetic.main.activity_navi_map.*
import java.util.*


open class NaviMapActivity : AppCompatActivity() {
    companion object {
        //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
        private const val BACK_LOCATION_PERMISSION =
            "android.permission.ACCESS_BACKGROUND_LOCATION"
    }

    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private val needCheckBackLocation = false
    private lateinit var aMap: AMap
    private lateinit var locationStyle: MyLocationStyle
    private lateinit var locationClient: AMapLocationClient
    private lateinit var locationOption: AMapLocationClientOption
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi_map)

        mapView.onCreate(savedInstanceState)
        aMap = mapView.map
        init()
        initLocation()
    }

    //初始化定位
    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     */
    private fun initLocation() {
        //初始化client
        locationClient = AMapLocationClient(this.applicationContext)
        locationOption = getDefaultOption()
        //设置定位参数
        locationClient.setLocationOption(locationOption)
        // 设置定位监听
        locationClient.setLocationListener { location ->
            if (null != location) {
                val sb = StringBuffer()
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.errorCode === 0) {
                    sb.append(
                        """
                            定位成功
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            定位类型: ${location.getLocationType().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            经    度    : ${location.getLongitude().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            纬    度    : ${location.getLatitude().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            精    度    : ${location.getAccuracy().toString()}米
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            提供者    : ${location.getProvider().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            速    度    : ${location.getSpeed().toString()}米/秒
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            角    度    : ${location.getBearing().toString()}
                            
                            """.trimIndent()
                    )
                    // 获取当前提供定位服务的卫星个数
                    sb.append(
                        """
                            星    数    : ${location.getSatellites().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            国    家    : ${location.getCountry().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            省            : ${location.getProvince().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            市            : ${location.getCity().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            城市编码 : ${location.getCityCode().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            区            : ${location.getDistrict().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            区域 码   : ${location.getAdCode().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            地    址    : ${location.getAddress().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            兴趣点    : ${location.getPoiName().toString()}
                            
                            """.trimIndent()
                    )
                    //定位完成的时间
                } else {
                    //定位失败
                    sb.append(
                        """
                            定位失败
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            错误码:${location.getErrorCode().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            错误信息:${location.getErrorInfo().toString()}
                            
                            """.trimIndent()
                    )
                    sb.append(
                        """
                            错误描述:${location.getLocationDetail().toString()}
                            
                            """.trimIndent()
                    )
                }
                sb.append("***定位质量报告***").append("\n")
                sb.append("* WIFI开关：")
                    .append(if (location.getLocationQualityReport().isWifiAble()) "开启" else "关闭")
                    .append("\n")
                sb.append("* GPS状态：")
                    .append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus()))
                    .append("\n")
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites())
                    .append("\n")
                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType())
                    .append("\n")
                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime())
                    .append("\n")
                sb.append("****************").append("\n")
                //定位之后的回调时间

                //解析定位结果，
                val result = sb.toString()
                Log.e("NaviMap", result)
            } else {
                val result = "定位失败"
                Log.e("NaviMap", result)
            }
        }

        locationClient.startLocation()
    }

    private fun init() {
        locationStyle = MyLocationStyle().apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW)
        }

        aMap.myLocationStyle = locationStyle
        aMap.setOnMapLoadedListener {
            setUp(aMap)
        }
    }

    private fun setUp(amap: AMap) {
        val uiSettings = amap.uiSettings
        amap.showIndoorMap(true)
        uiSettings.isCompassEnabled = true
        uiSettings.isScaleControlsEnabled = true
        uiSettings.isMyLocationButtonEnabled = false
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onResume() {
        try {
            super.onResume()
            mapView.onResume()
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(*needPermissions)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     */
    private fun getDefaultOption(): AMapLocationClientOption {
        val mOption = AMapLocationClientOption()
        mOption.locationMode =
            AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.isGpsFirst = false //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.interval = 2000 //可选，设置定位间隔。默认为2秒
        mOption.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是true
        mOption.isOnceLocation = false //可选，设置是否单次定位。默认是false
        mOption.isOnceLocationLatest =
            false //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.isSensorEnable = false //可选，设置是否使用传感器。默认是false
        mOption.isWifiScan =
            true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
        mOption.geoLanguage =
            AMapLocationClientOption.GeoLanguage.DEFAULT //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption
    }

    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private fun getGPSStatusString(statusCode: Int): String {
        var str = ""
        when (statusCode) {
            AMapLocationQualityReport.GPS_STATUS_OK -> str = "GPS状态正常"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER -> str =
                "手机中没有GPS Provider，无法进行GPS定位"
            AMapLocationQualityReport.GPS_STATUS_OFF -> str = "GPS关闭，建议开启GPS，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_MODE_SAVING -> str =
                "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION -> str = "没有GPS定位权限，建议开启gps定位权限"
        }
        return str
    }
    /*************************************** 权限检查******************************************************/

    /*************************************** 权限检查 */
    /**
     * 需要进行检测的权限数组
     */
    private var needPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        BACK_LOCATION_PERMISSION
    )

    private val PERMISSON_REQUESTCODE = 0

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private var isNeedCheck = true


    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private fun checkPermissions(vararg permissions: String) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && applicationInfo.targetSdkVersion >= 23) {
                val needRequestPermissonList =
                    findDeniedPermissions(permissions as Array<String>)
                if (null != needRequestPermissonList
                    && needRequestPermissonList.isNotEmpty()
                ) {
                    try {
                        val array =
                            needRequestPermissonList.toTypedArray()
                        val method = javaClass.getMethod(
                            "requestPermissions", *arrayOf<Class<*>?>(
                                Array<String>::class.java,
                                Int::class.javaPrimitiveType
                            )
                        )
                        method.invoke(this, array, 0)
                    } catch (e: Throwable) {
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private fun findDeniedPermissions(permissions: Array<String>): List<String>? {
        try {
            val needRequestPermissonList: MutableList<String> =
                ArrayList()
            if (Build.VERSION.SDK_INT >= 23 && applicationInfo.targetSdkVersion >= 23) {
                for (perm in permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                        || shouldShowMyRequestPermissionRationale(perm)
                    ) {
                        if (!needCheckBackLocation
                            && BACK_LOCATION_PERMISSION == perm
                        ) {
                            continue
                        }
                        needRequestPermissonList.add(perm)
                    }
                }
            }
            return needRequestPermissonList
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    private fun checkMySelfPermission(perm: String): Int {
        try {
            val method = javaClass.getMethod(
                "checkSelfPermission", *arrayOf<Class<*>>(
                    String::class.java
                )
            )
            return method.invoke(this, perm) as Int
        } catch (e: Throwable) {
        }
        return -1
    }

    private fun shouldShowMyRequestPermissionRationale(perm: String): Boolean {
        try {
            val method = javaClass.getMethod(
                "shouldShowRequestPermissionRationale", *arrayOf<Class<*>>(
                    String::class.java
                )
            )
            return method.invoke(this, perm) as Boolean
        } catch (e: Throwable) {
        }
        return false
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private fun verifyPermissions(grantResults: IntArray): Boolean {
        try {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(grantResults)) {
                        showMissingPermissionDialog()
                        isNeedCheck = false
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private fun showMissingPermissionDialog() {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
            builder.setMessage("当前应用缺少必要权限。\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限")

            // 拒绝, 退出应用
            builder.setNegativeButton(
                "取消"
            ) { dialog, which ->
                try {
                    finish()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            builder.setPositiveButton(
                "设置"
            ) { dialog, which ->
                try {
                    startAppSettings()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            builder.setCancelable(false)
            builder.show()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private fun startAppSettings() {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            )
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}