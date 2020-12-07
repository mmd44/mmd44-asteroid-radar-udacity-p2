package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.network.Network.asteroidService
import com.udacity.asteroidradar.repository.AsteroidsQueryFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.utils.setAtDayStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var calendar: Calendar

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private var _queryFilter = AsteroidsQueryFilter.SHOW_ALL

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod

    private val _status = MutableLiveData<LoadingStatus>()
    val status: LiveData<LoadingStatus>
        get() = _status

    init {
        calendar = Calendar.getInstance()
        calendar.setAtDayStart()

        _asteroids = asteroidsRepository.getAsteroidsStartingToday(calendar.time) as MutableLiveData<List<Asteroid>>

        refreshData()
        getPOD()
    }

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail
        get() = _navigateToAsteroidDetail

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _navigateToAsteroidDetail.value = null
    }

    fun updateFilter(filter: AsteroidsQueryFilter) {
        _queryFilter = filter
        viewModelScope.launch {
            updateDataWithFilter()
        }
    }

    private suspend fun updateDataWithFilter() {
        calendar = Calendar.getInstance()
        calendar.setAtDayStart()
        when (_queryFilter) {
            AsteroidsQueryFilter.SHOW_ALL -> {
                _asteroids.value = asteroidsRepository.getAllAsteroids()
            }
            AsteroidsQueryFilter.SHOW_TODAY -> {
                val startDate = calendar.time
                calendar.set(Calendar.HOUR_OF_DAY, 24)
                val endDate = calendar.time
                _asteroids.value = asteroidsRepository.getAsteroidsBetweenDates(startDate, endDate)
            }
            else -> {
                val startDate = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                val endDate = calendar.time
                _asteroids.value = asteroidsRepository.getAsteroidsBetweenDates(startDate, endDate)
            }
        }
    }

    private fun getPOD() {
        viewModelScope.launch {
            try {
                val pod = asteroidService.getPOD()
                _pod.value = pod
            } catch (e: Exception) {
                Timber.e("ErrorGettingPOD: ${e.stackTrace}")
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            // Shows loading only on first launch when the repo is empty; is there a better way instead of fetching from the DB to check if the repo is empty?
            if (asteroidsRepository.getAllAsteroids().isNullOrEmpty()) _status.value = LoadingStatus.LOADING
            try {
                asteroidsRepository.refreshAsteroids()
                _status.value = LoadingStatus.DONE
            } catch (e: Exception) {
                Timber.e("FailedOnDataRefresh: ${e.stackTrace}")
                _status.value = LoadingStatus.ERROR
            }
        }
    }
}

enum class LoadingStatus { LOADING, ERROR, DONE }