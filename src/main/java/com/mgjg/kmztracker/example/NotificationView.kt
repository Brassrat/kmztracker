package com.mgjg.kmztracker.example

/**
 * Created by marianne on 6/21/2015.
 */

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

import com.mgjg.kmztracker.R

class NotificationView : Activity() {
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.notification)

    val tv = findViewById(R.id.tv_notification) as TextView
    val data = intent.extras
    tv.text = data!!.getString("content")
  }
}