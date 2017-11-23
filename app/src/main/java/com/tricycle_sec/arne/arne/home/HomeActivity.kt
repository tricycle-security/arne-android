package com.tricycle_sec.arne.arne.home

import android.os.Bundle
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.auth.AuthUI
import java.util.*
import java.util.Arrays.asList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.ResultCodes
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import android.util.Log
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.security.spec.ECField


private val RC_SIGN_IN = 123
// Choose authentication providers
var providers = Arrays.asList(
        AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())

val mDatabase = FirebaseDatabase.getInstance().getReference("example/")

class Example(test: String = "") {
    var test = test
}

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)




        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        button2.setOnClickListener {
            val testobject = Example(editText.text.toString())
            mDatabase.setValue(testobject)
        }

    }
    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                home.text = "succes"
                readingData()

            } else {
                // Sign in failed, check response for error code
                home.text = "fail"
            }
        }
    }

    private fun readingData(){
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("DATA CHANGED", "ADDEDUSER LISTEN")
                Log.d("DATA CHANGED", dataSnapshot.key)
                val testobject = dataSnapshot.getValue<Example>(Example::class.java)
                home.text = testobject!!.test

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }


        }
        mDatabase.addValueEventListener(eventListener)
    }
}


