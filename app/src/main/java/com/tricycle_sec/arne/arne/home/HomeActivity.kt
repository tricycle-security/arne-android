package com.tricycle_sec.arne.arne.home

import android.os.Bundle
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.database.ChildEventListener
import com.tricycle_sec.arne.arne.firebase.UserAttendenceStatus
import com.tricycle_sec.arne.arne.firebase.UserGeneralInfo
import com.tricycle_sec.arne.arne.handlers.NavigationDrawerHandler
import kotlinx.android.synthetic.main.attendancy_item.view.*

class HomeActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener {

    private val myMap = HashMap<String, UserAttendenceStatus>()
    private var adapter : AttendancyAdapter = AttendancyAdapter(myMap)
    private var navigationDrawerHandler = NavigationDrawerHandler()
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.app_name, R.string.app_name)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        navigation_view.setNavigationItemSelectedListener(this)
        actionBarDrawerToggle.syncState()

        content_frame.layoutManager = LinearLayoutManager(this)
        content_frame.adapter = adapter

        getCurrentStatuss()
        getUsersGeneralInfo()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navigationDrawerHandler.handleNavigationItem(item, this)

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        return false
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    fun getUsersGeneralInfo() {
        val ref = getDatabaseReference(USER_PATH)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val addedUser = dataSnapshot.getValue<UserGeneralInfo>(UserGeneralInfo::class.java)
                if (addedUser != null) {
                    if(!myMap.containsKey(addedUser.uuid)){
                        myMap[addedUser.uuid] = UserAttendenceStatus()
                    }
                    myMap[addedUser.uuid]!!.fname = addedUser.fname
                    myMap[addedUser.uuid]!!.lname = addedUser.lname
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val changeduser = dataSnapshot.getValue<UserGeneralInfo>(UserGeneralInfo::class.java)
                if (changeduser != null) {
                    myMap[changeduser.uuid]!!.fname = changeduser.fname
                    myMap[changeduser.uuid]!!.lname = changeduser.lname
                    adapter.notifyDataSetChanged()
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
                    if(!myMap.containsKey(addedUser.uuid)){
                        myMap[addedUser.uuid] = UserAttendenceStatus()
                    }
                    myMap[addedUser.uuid]!!.onLocation = addedUser.onLocation
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val changedUser = dataSnapshot.getValue<CurrentStatus>(CurrentStatus::class.java)
                if (changedUser != null) {
                    myMap[changedUser.uuid]!!.onLocation = changedUser.onLocation
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    inner class AttendancyAdapter(val users: HashMap<String, UserAttendenceStatus>) : RecyclerView.Adapter<AttendanceViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
            val view = layoutInflater.inflate(R.layout.attendancy_item, parent, false)
            return AttendanceViewHolder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
            val userList = ArrayList(users.values)
            userList.sortBy { !it.onLocation }

            if(itemCount != 0) {
                holder.view.name.text = String.format("%s %s", userList[position].fname , userList[position].lname)
                holder.view.attendant_mark.visibility = if(userList[position].onLocation) View.VISIBLE else View.GONE
                holder.view.not_attendant_mark.visibility = if(userList[position].onLocation) View.GONE else View.VISIBLE
            }
        }

        override fun getItemCount() = if(users != null) users.count() else 0
    }

    inner class AttendanceViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}