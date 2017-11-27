package com.tricycle_sec.arne.arne.home

import android.os.Bundle
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.content.res.Resources
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tricycle_sec.arne.arne.firebase.CurrentStatus
import com.tricycle_sec.arne.arne.firebase.Example
import com.tricycle_sec.arne.arne.login.LoginActivity
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.database.ChildEventListener
import com.tricycle_sec.arne.arne.firebase.UserAttendenceStatus
import com.tricycle_sec.arne.arne.firebase.UserGeneralInfo
import kotlinx.android.synthetic.main.attendancy_item.view.*


class HomeActivity : BaseActivity() {


    private val myMap = HashMap<String, UserAttendenceStatus>()
    private var adapter : AttendancyAdapter = AttendancyAdapter(myMap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        //getExampleData()
        //getTestData()
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
                //example_text.text =  if(homeText != null) homeText else ""
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        getDatabaseReference(EXAMPLE_PATH).addValueEventListener(eventListener)
    }

    fun getTestData() {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //test_text.text =  dataSnapshot.value as String
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
                    println("changeduser: " + changeduser.fname)
                    println("changeduser: " + changeduser.lname)
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
                    println("addeduser: " + addedUser.uuid)
                    println("addeduser: " + addedUser.onLocation)
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
                    println("userchanged: " + changedUser.uuid)
                    println("userchanged: " + changedUser.onLocation)
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
            println(users)
            println(userList)
            if(itemCount != 0) {
                holder.view.name.text = String.format("%s %s", userList[position].fname , userList[position].lname)
                if (userList[position].onLocation.equals("False")){
                    holder.view.setBackgroundColor(resources.getColor(R.color.red))
                }else{
                    holder.view.setBackgroundColor(resources.getColor(R.color.green))
                }

                println(position)
            }
        }

        override fun getItemCount() = if(users != null) users.count() else 0
    }

    inner class AttendanceViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}


