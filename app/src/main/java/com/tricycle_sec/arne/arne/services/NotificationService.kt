package com.tricycle_sec.arne.arne.services

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.home.HomeActivity
import android.app.PendingIntent
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.app.NotificationChannel
import android.os.Build
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.firebase.Alert
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import java.util.*


class NotificationService : IntentService("NotificationService") {

    private var status: Boolean = true

    override fun onHandleIntent(intent: Intent?) {

        if(!status) {
            return
        }

        val reference = FirebaseDatabase.getInstance().getReference(BaseActivity.ALERT_PATH)
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val alert = dataSnapshot.getValue<Alert>(Alert::class.java)

                if(alert!!.active) {
                    val intent = Intent(this@NotificationService, HomeActivity::class.java)
                    val resultPendingIntent = PendingIntent.getActivity(this@NotificationService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channelId = "channel_01"
                    val alertText = String.format("%s \nLocatie: %s", alert.description, alert.location)

                    val notification = NotificationCompat.Builder(this@NotificationService)
                            .setSmallIcon(R.drawable.ic_priority_high)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(alertText).setBigContentTitle(alert.kind))
                            .setVibrate(longArrayOf(1000, 2000, 3000, 4000, 5000))
                            .setLights(Color.RED, 500, 500)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setChannelId(channelId)

                    notification.setContentIntent(resultPendingIntent)
                    notificationManager.notify(alert.time.toInt() , notification.build())
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onDestroy() {
        status = false
        super.onDestroy()
    }
}
