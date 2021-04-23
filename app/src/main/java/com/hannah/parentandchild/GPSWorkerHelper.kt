package com.hannah.parentandchild

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.work.*
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class GPSWorkerHelper(var mContext : Context) {

    val uniqWorkName = "gpsworker"
    lateinit var sharedPreferences :SharedPreferences;
    init{
        sharedPreferences =  mContext.getSharedPreferences("ParentChildPref", Context.MODE_PRIVATE);
    }
    /**
     * Worker 등록, UUID저장
     * @param ctx
     */
    fun start(ctx: Context?) {
        val worker = createWorkRequest()
        sharedPreferences.edit().putString(uniqWorkName, worker.getId().toString()).apply()
        val workManager = WorkManager.getInstance()
        workManager.enqueueUniquePeriodicWork(uniqWorkName, ExistingPeriodicWorkPolicy.REPLACE, worker)
    }

    private fun getContsratint(): Constraints? {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // 와이파이 연결된 경우(돈 안내는거) // 다른값(NOT_REQUIRED, CONNECTED, NOT_ROAMING, METERED)
            .setRequiresBatteryNotLow(true) // 배터리가 부족하지 않는 경우
            .setRequiresStorageNotLow(true) // 저장소가 부족하지 않는 경우
            .build()
    }

    private val DURATION_HOUR = 1L
    private fun createWorkRequest(): PeriodicWorkRequest {
        return if (Build.VERSION.SDK_INT >= 26) {
            val du = Duration.ofHours(DURATION_HOUR.toLong())
            PeriodicWorkRequest.Builder(GPSWorker::class.java, du)
                .setConstraints(getContsratint()!!)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
        } else {
            PeriodicWorkRequest.Builder(GPSWorker::class.java, DURATION_HOUR, TimeUnit.HOURS)
                .setConstraints(getContsratint()!!)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
        }
    }

    /**
     * Worker 삭제 UUID로 삭제, Unique이름으로 삭제.
     * 더이상 구매내역정보를 크롤링하지 않을 경우에 사용한다.
     * 1. 사용자 로그아웃
     * 2. 설정에서 구매내역수집 해제
     * @param ctx
     */
    fun cancelWork(ctx: Context?) {
        val enableworker: String = sharedPreferences.getString(uniqWorkName, "").toString()
        try {
            if (enableworker.isNotEmpty()){
                WorkManager.getInstance().cancelWorkById(UUID.fromString(enableworker))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        WorkManager.getInstance().cancelUniqueWork(uniqWorkName)
        sharedPreferences.edit().remove(uniqWorkName).apply()
    }
}