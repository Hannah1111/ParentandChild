package com.hannah.parentandchild

import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GPSWorker (context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val mContext = context

    //위치 가져올때 필요
    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(mContext)
    }

    override suspend fun doWork(): Result {
        return try {
            checkDistance()
        } catch (err: Exception) {
            err.printStackTrace()
            Result.failure()
        }
    }

    private fun checkDistance() : Result {
        try {
            //마지막 위치를 가져오는데에 성공한다면
            mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if( task.isSuccessful){
                    task.result?.let{ aLocation ->
                        val fromLat = aLocation.latitude
                        val fromLng = aLocation.longitude

                        val results = FloatArray(1)
                        ALog.e("---- DISTANCE >> ${fromLat}, ${fromLng}")
                    }
                }else{
                    ALog.e("--- FAIL TASK")
                }
            }
        }catch (err: SecurityException) {
            err.printStackTrace()
            ALog.e("--- FAIL SecurityException")
        }
        return Result.success()
    }
}