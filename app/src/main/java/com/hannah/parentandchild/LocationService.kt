package com.hannah.parentandchild

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.provider.Contacts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat


class LocationService : Service() , LocationListener {

//    private val mBinder: IBinder = LocalBinder()
    var iLoopValue = 0

    var iThreadInterval = 25000

    var bThreadGo = true


    var locationMgr: LocationManager? = null
    override fun onBind(intent: Intent?): IBinder? {
        ALog.e("-- onBind ")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notichannerId = "parents_chiled_noty"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = notificationManager.getNotificationChannel(notichannerId)

            val channelMessage = NotificationChannel(notichannerId , "위치찾기", NotificationManager.IMPORTANCE_HIGH)
            channelMessage.description = "다양한 할인정보 및 이벤트 소식을 받을 수 있습니다."
            channelMessage.group = "부모자식"
            channelMessage.lightColor = Color.GREEN
            channelMessage.enableLights(true)
            channelMessage.enableVibration(true)
            channelMessage.vibrationPattern = longArrayOf(200, 100, 200, 100)
            channelMessage.lockscreenVisibility = Notification.VISIBILITY_PUBLIC


            if (channel == null) {
                channel = channelMessage
                notificationManager.createNotificationChannel(channel)
            }
            val notification: Notification = NotificationCompat.Builder(this, notichannerId).build()
            startForeground(1, notification)
        }
        ALog.e("-- onCreate ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ALog.e("-- onStartCommand ")
        ALog.e("-- startSERVICE ")
        bThreadGo = true
        locationMgr = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(checkPermission()){
            locationMgr!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 180000, 0f, this)
        }
        setGpsPosition()
        return START_STICKY
    }

    override fun onLocationChanged(location: Location) {
        try {
            ALog.e(">onLocationChanged : ${location.latitude}, ${location.longitude}")
            positionSaveProc()
        } catch (e: java.lang.Exception) {
            ALog.e(">onLocationChanged : $e")
        }
    }

//    class LocalBinder : Binder() {
//        val service: LocationService
//            get() = this
//    }

    interface ICallback

    private var mCallback: ICallback? = null

    fun registerCallback(cb: ICallback?) {
        mCallback = cb
    }

    var mRun = Runnable {
        try {
            while (bThreadGo) {
                ALog.e(">mRun")
                iLoopValue++
                Thread.sleep(iThreadInterval.toLong())
                if (iLoopValue > 100000) iLoopValue = 0

                positionSaveProc()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun positionSaveProc() {
        try {
            var dLatitude = 0.0
            var dLongitude = 0.0
            if (locationMgr != null && checkPermission()) {
                val lcPosition =
                    locationMgr!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lcPosition != null) {
                    dLatitude = lcPosition.latitude
                    dLongitude = lcPosition.longitude
                    ALog.e(">positionSaveProc : lat($dLatitude), lot($dLongitude)")
                    if (dLatitude != 0.0 && dLongitude != 0.0) {
                        ALog.e(">positionSaveProc : lat($dLatitude), lot($dLongitude) : GPS")
                        setLocationProvider("GPS")
                    } else {
                        setLocationProvider("NETWORK")
                    }
                } else {
                    ALog.e(">positionSaveProc : NETWORK ")
                    setLocationProvider("NETWORK")
                }
            }
        } catch (e: java.lang.Exception) {
            ALog.e(">positionSaveProc err : $e")
        }
    }

    @Synchronized
    fun setLocationProvider(parmOption: String) {
        if (locationMgr == null) return
        if (parmOption == "NETWORK") {
            ALog.e(">setLocationProvider NETWORK")
            if(checkPermission()){
                locationMgr!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0f, this)
            }else{
                setGpsPosition()
            }
        } else if (parmOption == "GPS") {
            ALog.e(">setLocationProvider GPS")
            if (checkPermission()){
                locationMgr!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, this)
            }
        }
    }

    fun checkPermission() :Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    
    @Synchronized
    fun setGpsPosition() {
        try {
            ALog.e(">setGpsPosition :")
            if (locationMgr == null) return
            if(checkPermission()){
                val lcPosition = locationMgr!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lcPosition != null) {
                    ALog.e(">setGpsPosition : lat(" + lcPosition.latitude + "), lot(" + lcPosition.longitude + ")")
                } else {

                }
            }
        } catch (e: Exception) {
            ALog.e(">setGpsPosition : error : $e")
        }
    }

    @Synchronized
    fun locationChangedProc(location: Location?) {
        try {
            ALog.e(">locationChangedProc : ")
            if (location == null) {
                return
            }
            val lat = location.latitude
            val lon = location.longitude
            if (lat > 1 && lon > 1) {
                ALog.e(">locationChangedProc : dUserContactLatitude : $lat")
                ALog.e(">locationChangedProc : dUserContactLongitude : $lon")

            } else {
                ALog.e(">locationChangedProc : gps : $lon")
            }
        } catch (e: java.lang.Exception) {
            ALog.e(">locationChangedProc : $e")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        }
    }

}