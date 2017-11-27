package com.tricycle_sec.arne.arne.home

import android.os.Bundle
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import com.tricycle_sec.arne.arne.firebase.Example
import com.tricycle_sec.arne.arne.login.LoginActivity
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.database.ChildEventListener
import com.tricycle_sec.arne.arne.firebase.UserGeneralInfo
import java.sql.SQLOutput


class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getExampleData()
        getTestData()
        getCurrentStatuss()
        getUsersGeneralInfo()
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

    fun getUsersGeneralInfo() {
        val ref = getDatabaseReference(USER_PATH)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val addedUser = dataSnapshot.getValue<UserGeneralInfo>(UserGeneralInfo::class.java)
                if (addedUser != null) {
                    println("addeduser: " + addedUser.fname)
                    println("addeduser: " + addedUser.lname)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val changeduser = dataSnapshot.getValue<UserGeneralInfo>(UserGeneralInfo::class.java)
                if (changeduser != null) {
                    println("changeduser: " + changeduser.fname)
                    println("changeduser: " + changeduser.lname)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getCurrentStatuss() {
        val ref = getDatabaseReference(STATUS_PATH)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val addedUser = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)
                if (addedUser != null) {
                    println("addeduser: " + addedUser.uuid)
                    println("addeduser: " + addedUser.onLocation)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val changedUser = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)
                if (changedUser != null) {
                    println("userchanged: " + changedUser.uuid)
                    println("userchanged: " + changedUser.onLocation)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}


