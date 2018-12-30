package com.mgjg.kmztracker

import android.app.Application

class KmzTrackerApp : Application() {

  override fun onCreate() {
    super.onCreate()
    instance = this
    AppPreferences.makeInstance(this)
  }

  fun getInstance(): KmzTrackerApp? {
    return instance
  }

  companion object {

    private var instance: KmzTrackerApp? = null
  }

}
