package com.tricycle_sec.arne.arne.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tricycle_sec.arne.arne.login.LoginActivity

open class BaseActivity : AppCompatActivity() {

    companion object {
        val USER_PATH = "userinfo/usergeninfo"
        val USER_STATUS_PATH = "userinfo/userstatus/%s/enabled"
        val STATUS_PATH = "currentstatus/"
        val ALERT_PATH = "alerts/"
        val RESPONSE_PATH = "alerts/%s/responders/%s"
    }

    fun getDatabaseReference(path: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().getReference(path)
    }

    fun checkUserStatus(title : String, message : String, button : String) {
        val reference = getDatabaseReference(String.format(USER_STATUS_PATH, FirebaseAuth.getInstance().currentUser!!.uid))
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val status = dataSnapshot.value as Boolean
                if(!status) {
                    AlertDialog.Builder(this@BaseActivity)
                            .setTitle(title)
                            .setMessage(message)
                            .setNeutralButton(button, { dialog, which ->  FirebaseAuth.getInstance().signOut()
                                this@BaseActivity.startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
                                dialog.dismiss()})
                            .create()
                            .show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}