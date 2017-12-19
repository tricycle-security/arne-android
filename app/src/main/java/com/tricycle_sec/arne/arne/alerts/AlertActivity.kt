package com.tricycle_sec.arne.arne.alerts

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
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

class AlertActivity : BaseActivity() {

    private var mappedAlerts: MutableMap<String , Alert> = mutableMapOf()
    private var alerts = mutableListOf<Alert>()
    private var adapter: AlertAdapter = AlertAdapter(alerts)
    private val alertRef = FirebaseDatabase.getInstance().getReference(BaseActivity.ALERT_PATH)
    private val alertListener = alertRef.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)!!
            mappedAlerts.put(alert.id, alert)
            alerts = mappedAlerts.map { it.value }.toMutableList()
            adapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)!!
            mappedAlerts.put(alert.id, alert)
            alerts = mappedAlerts.map { it.value }.toMutableList()
            adapter.notifyDataSetChanged()
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val alert = dataSnapshot.getValue<Alert>(Alert::class.java)!!
            mappedAlerts.remove(alert.id)
            alerts = mappedAlerts.map { it.value }.toMutableList()
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

    inner class AlertAdapter(alerts: MutableList<Alert>) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
            val view = layoutInflater.inflate(R.layout.alert_item, parent, false)
            return AlertViewHolder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
            alerts.sortBy { !it.active }
            holder.view.title.text = alerts[position].kind
            holder.view.responders.text = String.format(getString(R.string.alert_responders), alerts[position].responders.size)
            holder.view.item_overlay.visibility = if(alerts[position].active) View.GONE else View.VISIBLE

            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val cal = Calendar.getInstance()
            cal.timeInMillis = alerts[position].time
            val time = sdf.format(cal.time)

            holder.view.date.text = time
        }

        override fun getItemCount() = alerts.size

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
