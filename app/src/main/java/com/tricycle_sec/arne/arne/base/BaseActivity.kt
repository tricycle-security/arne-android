package com.tricycle_sec.arne.arne.base

import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.FirebaseDatabase
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {
        val RC_SIGN_IN = 1
        val providers = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())
        val mDatabase = FirebaseDatabase.getInstance().getReference("example/")
    }


    override fun onBackPressed() {
    }
}