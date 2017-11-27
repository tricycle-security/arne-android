package com.tricycle_sec.arne.arne.base

import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import com.tricycle_sec.arne.arne.firebase.Example
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {
        val RC_SIGN_IN = 1
        val providers = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())
        val EXAMPLE_PATH = "example/"
        val TEST_PATH = "test/test"
        val USER_PATH = "userinfo/usergeninfo"
        val STATUS_PATH = "currentstatus/"
    }

    override fun onBackPressed() {
    }

    fun getDatabaseReference(path: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().getReference(path)
    }
}