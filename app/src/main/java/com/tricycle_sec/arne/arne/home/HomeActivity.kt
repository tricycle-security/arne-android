package com.tricycle_sec.arne.arne.home

import android.os.Bundle
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import com.tricycle_sec.arne.arne.firebase.Example
import com.tricycle_sec.arne.arne.login.LoginActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getExampleData()
        getTestData()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_header, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getExampleData() {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val homeText = dataSnapshot.getValue(Example::class.java)?.test
                example_text.text =  if(homeText != null) homeText else ""
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        getDatabaseReference(EXAMPLE_PATH).addValueEventListener(eventListener)
    }

    fun getTestData() {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                test_text.text =  dataSnapshot.value as String
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        getDatabaseReference(TEST_PATH).addValueEventListener(eventListener)
    }

    fun getCurrentStatus() {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                test_text.text =  dataSnapshot.getValue(CurrentStatus::class.java).uuid
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        getDatabaseReference(STATUS_PATH).addValueEventListener(eventListener)
    }
}


