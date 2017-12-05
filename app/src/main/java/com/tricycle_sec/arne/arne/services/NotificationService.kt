package com.tricycle_sec.arne.arne.services

import android.app.IntentService
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.home.HomeActivity
import android.app.PendingIntent
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.content.ComponentName
import android.support.v4.app.BundleCompat
import android.widget.RemoteViews
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.firebase.Alert
import com.tricycle_sec.arne.arne.response.ResponseActivity

class NotificationService : IntentService("NotificationService") {

    companion object {
        var status: Boolean = true
        val ALERT = "ALERT"
    }

    override fun onHandleIntent(intent: Intent?) {

        if(!status) {
            return
        }

        val reference = FirebaseDatabase.getInstance().getReference(BaseActivity.ALERT_PATH)
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val alert = dataSnapshot.getValue<Alert>(Alert::class.java)

                if(alert!!.active) {
                    val intent = Intent(this@NotificationService, ResponseActivity::class.java)
                    intent.putExtra(ALERT, alert)

                    val currentUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
                    val resultPendingIntent = PendingIntent.getActivity(this@NotificationService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channelId = "channel_01"
                    val alertText = String.format("%s \nLocatie: %s", alert.description, alert.location)
                    val responded = alert.responders.contains(currentUser.uid)

                    val contentView = RemoteViews(packageName, R.layout.custom_notification)
                    contentView.setTextViewText(R.id.title, alert.kind)
                    contentView.setTextViewText(R.id.description, alertText)

                    val notification = NotificationCompat.Builder(this@NotificationService)
                            .setSmallIcon(R.drawable.ic_priority_high)
                            .setCustomBigContentView(contentView)
                            .setOngoing(true)
                            .setAutoCancel(true)
                            .setContentTitle(alert.kind)
                            .setContentText(alertText)
                            .setVibrate(longArrayOf(1000, 2000, 3000, 4000, 5000))
                            .setLights(Color.RED, 500, 500)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setChannelId(channelId)

                    notification.setContentIntent(resultPendingIntent)
                    if(!responded) {
                        notificationManager.notify(alert.time.toInt(), notification.build())
                    }
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
