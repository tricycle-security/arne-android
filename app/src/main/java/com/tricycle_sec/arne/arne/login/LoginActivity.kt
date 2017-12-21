package com.tricycle_sec.arne.arne.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.home.HomeActivity
import java.util.*

class LoginActivity : BaseActivity() {

    private val RC_SIGN_IN = 1
    private val providers = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            when (resultCode) {
                Activity.RESULT_OK -> checkStatus()
                else -> promptLogin()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkStatus() {
        val reference = getDatabaseReference(String.format(USER_STATUS_PATH, FirebaseAuth.getInstance().currentUser!!.uid))
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val status = dataSnapshot.value as Boolean
                if(status) {
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                }else {
                    AlertDialog.Builder(this@LoginActivity, R.style.CustomAlertDialogStyle)
                            .setTitle(getString(R.string.login_warning_title))
                            .setMessage(getString(R.string.login_warning_message))
                            .setNeutralButton(getString(R.string.ok), { dialog, which ->  FirebaseAuth.getInstance().signOut()
                                dialog.dismiss()
                                promptLogin()})
                            .create()
                            .show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkSession() {
        when (FirebaseAuth.getInstance().currentUser) {
            null -> promptLogin()
            else -> checkStatus()
        }
    }

    private fun promptLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setPrivacyPolicyUrl(getString(R.string.privacy_url))
                        .setAllowNewEmailAccounts(false)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }
}