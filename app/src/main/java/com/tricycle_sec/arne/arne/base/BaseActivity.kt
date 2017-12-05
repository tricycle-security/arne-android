package com.tricycle_sec.arne.arne.base

import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {
        val RC_SIGN_IN = 1
        val providers = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())
        val USER_PATH = "userinfo/usergeninfo"
        val STATUS_PATH = "currentstatus/"
        val ALERT_PATH = "alerts/"
        val RESPONSE_PATH = "alerts/%s/responders/%s"
    }

    fun getDatabaseReference(path: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().getReference(path)
    }
}