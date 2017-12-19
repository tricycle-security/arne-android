package com.tricycle_sec.arne.arne.alerts

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.firebase.Alert
import com.tricycle_sec.arne.arne.response.ResponseActivity
import com.tricycle_sec.arne.arne.services.NotificationService
import kotlinx.android.synthetic.main.activity_alert.*
import kotlinx.android.synthetic.main.alert_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AlertActivity : BaseActivity() {

    private var mappedAlerts: HashMap<String , Alert> = hashMapOf()
    private var adapter: AlertAdapter = AlertAdapter(mappedAlerts)
    private val alertRef = FirebaseDatabase.getInstance().getReference(BaseActivity.ALERT_PATH)
    private val currentUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
    private val alertListener = alertRef.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)!!
            mappedAlerts.put(alert.id, alert)
            adapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)!!
            mappedAlerts.put(alert.id, alert)
            adapter.notifyDataSetChanged()
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)!!
            mappedAlerts.remove(alert.id)
            adapter.notifyDataSetChanged()
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

        override fun onCancelled(databaseError: DatabaseError) {}
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(getString(R.string.title_alerts))

        content_frame.layoutManager = LinearLayoutManager(this@AlertActivity)
        content_frame.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        alertRef.addChildEventListener(alertListener)
    }

    override fun onPause() {
        super.onPause()
        alertRef.removeEventListener(alertListener)
    }

    inner class AlertAdapter(val alertMap: HashMap<String, Alert>) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {
        var alerts = mutableListOf<Alert>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
            val view = layoutInflater.inflate(R.layout.alert_item, parent, false)
            alerts = alertMap.map { it.value }.toMutableList()
            return AlertViewHolder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
            alerts = alertMap.map { it.value }.toMutableList()
            alerts.sortByDescending { it.time }
            alerts.sortByDescending { it.active }
            holder.view.title.text = alerts[position].kind
            holder.view.responders.text = String.format(getString(R.string.alert_responders), alerts[position].responders.size)
            holder.view.item_overlay.visibility = if(alerts[position].active) View.GONE else View.VISIBLE

            if(alertMap[alerts[position].id]!!.responders.containsKey(currentUser.uid)) {
                holder.view.responded.text = if(alertMap[alerts[position].id]!!.responders[currentUser.uid]!!.responding) getString(R.string.overview_responding) else getString(R.string.overview_not_responding)
            }else {
                holder.view.responded.text = ""
            }

            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val cal = Calendar.getInstance()
            cal.timeInMillis = alerts[position].time
            val time = sdf.format(cal.time)

            holder.view.date.text = time
        }

        override fun getItemCount() = mappedAlerts.size

        private fun onItemSelected(position: Int) {
            val item = alerts[position]

            startActivity(Intent(this@AlertActivity, ResponseActivity::class.java)
                    .putExtra(NotificationService.ALERT , item))
        }

        inner class AlertViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            init {
                view.setOnClickListener { onItemSelected(adapterPosition) }
            }
        }
    }
}
