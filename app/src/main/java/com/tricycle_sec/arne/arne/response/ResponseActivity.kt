package com.tricycle_sec.arne.arne.response

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.firebase.Alert
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import com.tricycle_sec.arne.arne.firebase.Response
import com.tricycle_sec.arne.arne.services.NotificationService
import kotlinx.android.synthetic.main.activity_response.*
import kotlinx.android.synthetic.main.alert_item.*
import java.text.SimpleDateFormat
import java.util.*

class ResponseActivity : BaseActivity() {

    lateinit var alert: Alert
    private val currentUser = FirebaseAuth.getInstance().currentUser as FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.title_alert)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_PLAY_SOUND)
            }
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_PLAY_SOUND)
        }

        alert = intent.getSerializableExtra(NotificationService.ALERT) as Alert

        val sdf = SimpleDateFormat("hh:mm:ss")
        val cal = Calendar.getInstance()
        cal.timeInMillis = alert.time
        val time = sdf.format(cal.time)

        alert_type.text = Html.fromHtml(getString(R.string.response_header_time) + " <b>" + time + "</b> " + getString(R.string.response_header_kind) + " <b>" + alert.kind + "</b> " + getString(R.string.response_header_location) + " <b>" + alert.location + "</b>")
        alert_desciption.text = alert.description
        alert_desciption_header.visibility = if (alert.description.isNotEmpty()) View.VISIBLE else View.GONE

        val responded = alert.responders.containsKey(currentUser.uid)
        onLocationListener(alert.active, responded)
        completed_text.visibility = if (responded) View.VISIBLE else View.GONE

        if (responded) {
            val responding = alert.responders.get(currentUser.uid)!!.responding
            completed_text.text = if (responding) getString(R.string.response_responded_true) else getString(R.string.response_responded_false)
        }

        accept_button.setOnClickListener { postResponse(true) }
        reject_button.setOnClickListener { postResponse(false) }
        checkUserStatus(getString(R.string.login_warning_title), getString(R.string.login_warning_message), getString(R.string.ok))
    }

    override fun onResume() {
        super.onResume()
        NotificationService.status = false
    }

    override fun onPause() {
        super.onPause()
        NotificationService.status = true
    }

    private fun postResponse(responding: Boolean) {
        val message = if (responding) getString(R.string.response_warning_true) else getString(R.string.response_warning_false)
        val reference = getDatabaseReference(String.format(RESPONSE_PATH, alert.id, currentUser.uid))

        AlertDialog.Builder(this)
                .setTitle(getString(R.string.response_warning_title))
                .setMessage(message)
                .setNegativeButton(getString(R.string.negative), { dialog, which -> dialog.dismiss() })
                .setPositiveButton(getString(R.string.positive), { dialog, which ->
                    reference.setValue(Response(responding, System.currentTimeMillis(), currentUser.uid))
                    dialog.dismiss()
                    completed_text.text = if (responding) getString(R.string.response_completed_true) else getString(R.string.response_completed_false)
                    completed_text.visibility = View.VISIBLE
                    button_layout.visibility = View.GONE
                })
                .create()
                .show()
    }

    private fun onLocationListener(alertActive: Boolean, responded: Boolean) {
        val userStatusRef = FirebaseDatabase.getInstance().getReference(BaseActivity.STATUS_PATH)

        userStatusRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val userStatus = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)!!
                if (userStatus.uuid == currentUser.uid) {
                    button_layout.visibility = if (userStatus.onLocation && alertActive && !responded) View.VISIBLE else View.GONE
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val userStatus = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)!!
                if (userStatus.uuid == currentUser.uid) {
                    button_layout.visibility = if (userStatus.onLocation && alertActive && !responded) View.VISIBLE else View.GONE
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
