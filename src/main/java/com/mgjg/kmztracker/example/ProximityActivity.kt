package com.mgjg.kmztracker.example

/**
 * Created by marianne on 6/21/2015.
 */

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.widget.Toast

import com.mgjg.kmztracker.R

class ProximityActivity : Activity() {

  internal var notificationTitle: String
  internal var notificationContent: String
  internal var tickerMessage: String

  override fun onCreate(savedInstanceState: Bundle?) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState)

    val proximity_entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true)

    if (proximity_entering) {
      Toast.makeText(baseContext, "Entering the region", Toast.LENGTH_LONG).show()
      notificationTitle = "Proximity - Entry"
      notificationContent = "Entered the region"
      tickerMessage = "Entered the region"
    } else {
      Toast.makeText(baseContext, "Exiting the region", Toast.LENGTH_LONG).show()
      notificationTitle = "Proximity - Exit"
      notificationContent = "Exited the region"
      tickerMessage = "Exited the region"
    }

    val notificationIntent = Intent(applicationContext, NotificationView::class.java)
    notificationIntent.putExtra("content", notificationContent)

    /** This is needed to make this intent different from its previous intents  */
    notificationIntent.data = Uri.parse("tel:/" + System.currentTimeMillis().toInt())

    /** Creating different tasks for each notification. See the flag Intent.FLAG_ACTIVITY_NEW_TASK  */
    val pendingIntent = PendingIntent.getActivity(
      applicationContext, 0, notificationIntent,
      PendingIntent.FLAG_ONE_SHOT
    ) // in example: Intent.FLAG_ACTIVITY_NEW_TASK);

    /** Getting the System service NotificationManager  */
    val nManager =
      applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /** Configuring notification builder to create a notification  */
    val notificationBuilder = NotificationCompat.Builder(applicationContext)
      .setWhen(System.currentTimeMillis())
      .setContentText(notificationContent)
      .setContentTitle(notificationTitle)
      .setSmallIcon(R.drawable.ic_launcher)
      .setAutoCancel(true)
      .setTicker(tickerMessage)
      .setContentIntent(pendingIntent)

    /** Creating a notification from the notification builder  */
    val notification = notificationBuilder.build()

    /** Sending the notification to system.
     * The first argument ensures that each notification is having a unique id
     * If two notifications share same notification id, then the last notification replaces the first notification
     */
    nManager.notify(System.currentTimeMillis().toInt(), notification)

    /** Finishes the execution of this activity  */
    finish()
  }
}