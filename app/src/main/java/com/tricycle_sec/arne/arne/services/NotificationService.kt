package com.tricycle_sec.arne.arne.services

import android.app.*
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.tricycle_sec.arne.arne.R
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.firebase.Alert
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import com.tricycle_sec.arne.arne.response.ResponseActivity
import android.net.Uri
import android.media.AudioManager




class NotificationService : Service() {

    private val alertListener : ChildEventListener =  object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)
            if(!alert!!.responders.containsKey(currentUser.uid)){
                notifyUser(alert!!)
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)
            if(!alert!!.responders.containsKey(currentUser.uid)){
                notifyUser(alert!!)
            }
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

        override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    companion object {
        var status: Boolean = true
        val ALERT = "ALERT"
        val currentUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(status) {
            onLocationListener()
        }
        return Service.START_STICKY
    }

    private fun onLocationListener() {
        val userStatusRef = FirebaseDatabase.getInstance().getReference(BaseActivity.STATUS_PATH)
        val alertRef = FirebaseDatabase.getInstance().getReference(BaseActivity.ALERT_PATH)

        userStatusRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val userStatus = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)
                if(userStatus!!.uuid == currentUser.uid){
                    alertListener(alertRef, userStatus!!.onLocation)
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val userStatus = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)
                if(userStatus!!.uuid == currentUser.uid){
                    alertListener(alertRef, userStatus!!.onLocation)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun alertListener(alertRef: DatabaseReference, onLocation: Boolean) {
        if(onLocation){
            alertRef.addChildEventListener(alertListener)
        }else{
            alertRef.removeEventListener(alertListener)
        }
    }

    override fun onDestroy() {
        status = false
        super.onDestroy()
    }

    private fun notifyUser(alert : Alert) {
        if(alert!!.active) {
            val intent = Intent(this@NotificationService, ResponseActivity::class.java)
            intent.putExtra(ALERT, alert)

            val resultPendingIntent = PendingIntent.getActivity(this@NotificationService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val sound = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm)
            val channelId = "channel_01"
            val alertText = String.format("%s \nLocatie: %s", alert.description, alert.location)

            val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            manager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 8, AudioManager.FLAG_PLAY_SOUND)

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
                    .setSound(sound)
                    .setLights(Color.RED, 500, 500)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setChannelId(channelId)

            notification.setContentIntent(resultPendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH))
            }

            val buildedNotification = notification.build()
            buildedNotification.flags = Notification.FLAG_INSISTENT
            buildedNotification.flags += Notification.FLAG_ONGOING_EVENT
            buildedNotification.flags += Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(alert.time.toInt(), buildedNotification)
        }
    }
}
