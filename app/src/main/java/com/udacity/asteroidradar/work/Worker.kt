package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.utils.setAtDayStart
import retrofit2.HttpException
import java.util.*

class Worker (appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)

        val calendar = Calendar.getInstance()
        calendar.setAtDayStart()

        return try {
            repository.refreshAsteroids()
            repository.deleteOldAsteroids(calendar.time)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}