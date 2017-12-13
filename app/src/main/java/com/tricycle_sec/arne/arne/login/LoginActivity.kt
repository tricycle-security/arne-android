package com.tricycle_sec.arne.arne.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
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
                Activity.RESULT_OK -> startActivity(Intent(this, HomeActivity::class.java))
                else -> promptLogin()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkSession() {
        when (FirebaseAuth.getInstance().currentUser) {
            null -> promptLogin()
            else -> startActivity(Intent(this, HomeActivity::class.java))
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