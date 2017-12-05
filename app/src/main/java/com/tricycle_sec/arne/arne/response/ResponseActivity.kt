package com.tricycle_sec.arne.arne.response

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Html
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.firebase.Alert
import com.tricycle_sec.arne.arne.firebase.Response
import com.tricycle_sec.arne.arne.services.NotificationService
import kotlinx.android.synthetic.main.activity_response.*
import java.text.SimpleDateFormat
import java.util.*

class ResponseActivity : BaseActivity() {

    lateinit var alert : Alert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)

        alert = intent.getSerializableExtra(NotificationService.ALERT) as Alert

        val sdf = SimpleDateFormat("hh:mm:ss")
        val cal = Calendar.getInstance()
        cal.timeInMillis = alert.time
        val time = sdf.format(cal.time)

        alert_header.text = Html.fromHtml(getString(R.string.response_header_time)+" <b>"+ time +"</b> "+ getString(R.string.response_header_kind) +" <b>"+ alert.kind +"</b> "+ getString(R.string.response_header_location) +" <b>" + alert.location + "</b>")
        alert_desciption.text = alert.description

        accept_button.setOnClickListener { postResponse(true) }
        reject_button.setOnClickListener { postResponse(false) }
    }

    private fun postResponse(responding: Boolean) {
        val currentUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
        val message = if(responding) getString(R.string.response_warning_true) else getString(R.string.response_warning_false)
        val reference = getDatabaseReference(String.format(RESPONSE_PATH, alert.id, currentUser.uid))

        AlertDialog.Builder(this)
                .setTitle(getString(R.string.response_warning_title))
                .setMessage(message)
                .setNegativeButton(getString(R.string.negative), {dialog, which -> dialog.dismiss()})
                .setPositiveButton(getString(R.string.positive), { dialog, which ->  reference.setValue(Response(responding, System.currentTimeMillis(), currentUser.uid))
                    dialog.dismiss()})
                .create()
                .show()
    }
}
