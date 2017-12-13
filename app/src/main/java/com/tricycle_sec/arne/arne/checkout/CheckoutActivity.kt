package com.tricycle_sec.arne.arne.checkout

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    val currentUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
    val path = String.format("%s%s", STATUS_PATH, currentUser.uid)
    val reference = getDatabaseReference(path)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(getString(R.string.title_checkout))

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val status = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)
                val statusColor = if(status!!.onLocation) ContextCompat.getColor(this@CheckoutActivity, R.color.green) else ContextCompat.getColor(this@CheckoutActivity, R.color.red)
                card_image.setColorFilter(statusColor)
                card_image.isClickable = status!!.onLocation
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        card_image.setOnClickListener { checkOutCard() }
    }

    fun checkOutCard() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.checkout_warning_title))
                .setMessage(getString(R.string.checkout_warning))
                .setNegativeButton(getString(R.string.negative), {dialog, which -> dialog.dismiss()})
                .setPositiveButton(getString(R.string.positive), { dialog, which ->  reference.setValue(CurrentStatus(currentUser.uid, false))
                    dialog.dismiss()})
                .create()
                .show()
    }
}
