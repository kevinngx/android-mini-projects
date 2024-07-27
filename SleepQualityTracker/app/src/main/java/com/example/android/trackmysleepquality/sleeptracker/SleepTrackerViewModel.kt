package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SleepTrackerViewModel(
  val database: SleepDatabaseDao,
  application: Application
) : AndroidViewModel(application) {

  private var viewModelJob = Job()

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  private var tonight = MutableLiveData<SleepNight?>()

  val nights = database.getAllNights()

  val nightsString = nights.map { nights ->
    formatNights(nights, application.resources)
  }

  private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()

  val navigateToSleepQuality: LiveData<SleepNight?>
    get() = _navigateToSleepQuality

  fun doneNavigating() {
    _navigateToSleepQuality.value = null
  }

  val startButtonVisible = tonight.map {
    it == null
  }
  val stopButtonVisible = tonight.map {
    it != null
  }
  val clearButtonVisible = nights.map {
    it?.isNotEmpty()
  }

  private val _navigateToSleepDataQuality = MutableLiveData<Long?>()
  val navigateToSleepDataQuality
    get() = _navigateToSleepDataQuality

  init {
    initializeTonight()
  }

  private fun initializeTonight() {
    uiScope.launch {
      // Work inside coroutine
      tonight.value = getTonightFromDatabase()
    }
  }

  private suspend fun getTonightFromDatabase(): SleepNight? {
    return withContext(Dispatchers.IO) {
      var night = database.getTonight()
      if (night?.endTimeMilli != night?.startTimeMilli) {
        night = null
      }
      night
    }
  }

  fun onStartTracking() {
    uiScope.launch {
      val newNight = SleepNight()
      insert(newNight)
      tonight.value = getTonightFromDatabase()
    }
  }

  private suspend fun insert(night: SleepNight) {
    withContext(Dispatchers.IO) {
      database.insert(night)
    }
  }

  fun onStopTracking() {
    uiScope.launch {
      val oldNight = tonight.value ?: return@launch
      oldNight.endTimeMilli = System.currentTimeMillis()
      update(oldNight)
      _navigateToSleepQuality.value = oldNight
    }
  }

  private suspend fun update(night: SleepNight) {
    withContext(Dispatchers.IO) {
      database.update(night)
    }
  }

  fun onClear() {
    uiScope.launch {
      clear()
      tonight.value = null
    }
  }

  private suspend fun clear() {
    withContext(Dispatchers.IO) {
      database.clear()
    }
  }

  fun onSleepNightClicked(id: Long) {
    _navigateToSleepDataQuality.value = id
  }

  fun onSleepDataQualityNavigated() {
    _navigateToSleepDataQuality.value = null
  }

}