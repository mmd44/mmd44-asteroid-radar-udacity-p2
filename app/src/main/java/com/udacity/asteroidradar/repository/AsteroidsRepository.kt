package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.main.LoadingStatus
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.utils.setAtDayStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import timber.log.Timber
import java.util.*

enum class AsteroidsQueryFilter { SHOW_ALL, SHOW_TODAY, SHOW_WEEK }

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    fun getAsteroidsStartingToday(date: Date): LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsStartingToday(date)) {
            it.asDomainModel()
        }

    suspend fun getAllAsteroids(): List<Asteroid> =
        database.asteroidDao.getSavedAsteroids().asDomainModel()


    suspend fun getAsteroidsBetweenDates(startDate: Date, endDate: Date): List<Asteroid> =
        database.asteroidDao.getAsteroidsBetweenDates(startDate, endDate).asDomainModel()


    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            // Is this where the JSON response should be handled in the current app architecture? (or better to parse it in the viewModel? the error is caught there anyways...)
            val strJSON = Network.asteroidService.getAsteroids().await()
            val jsonObject = JSONObject(strJSON)
            val asteroids = parseAsteroidsJsonResult(jsonObject)
            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
        }
    }

    suspend fun deleteOldAsteroids (date: Date) {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteBeforeDate(date)
        }
    }
}
