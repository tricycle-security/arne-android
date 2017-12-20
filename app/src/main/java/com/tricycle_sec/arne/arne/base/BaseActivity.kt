package com.tricycle_sec.arne.arne.base

import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.*

open class BaseActivity : AppCompatActivity() {

    companion object {
        val USER_PATH = "userinfo/usergeninfo"
        val STATUS_PATH = "currentstatus/"
        val ALERT_PATH = "alerts/"
        val RESPONSE_PATH = "alerts/%s/responders/%s"
    }

    fun getDatabaseReference(path: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().getReference(path)
    }
}