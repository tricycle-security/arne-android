package com.tricycle_sec.arne.arne.handlers

import android.content.Intent
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.checkout.CheckoutActivity
import com.tricycle_sec.arne.arne.home.HomeActivity
import com.tricycle_sec.arne.arne.login.LoginActivity
import com.tricycle_sec.arne.arne.services.NotificationService

class NavigationDrawerHandler {

    fun handleNavigationItem(item: MenuItem, activity: BaseActivity) {

        when (item.itemId) {
            R.id.nav_menu_logout -> {
                activity.stopService(HomeActivity.notificationIntent)
                FirebaseAuth.getInstance().signOut()
                activity.startActivity(Intent(activity, LoginActivity::class.java))
            }
            R.id.nav_menu_checkout -> activity.startActivity(Intent(activity, CheckoutActivity::class.java))
            else -> {
            }
        }
    }
}
