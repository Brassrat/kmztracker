package com.mgjg.kmztracker

import android.annotation.TargetApi
import android.content.res.Resources
import android.os.Build

/**
 * Created by marianne on 1/9/2016.
 */
object AppResources {

  val resources: Resources
    get() = MainActivity.instance.resources

  fun getColor(colorResource: Int): Int {
    try {
      return MainActivity.instance.resources.getColor(colorResource)
    } catch (e: Resources.NotFoundException) {

    }

    return 0
  }
}
