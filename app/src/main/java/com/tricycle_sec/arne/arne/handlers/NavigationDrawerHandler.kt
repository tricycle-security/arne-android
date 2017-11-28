package com.tricycle_sec.arne.arne.handlers

import android.content.Intent
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.tricycle_sec.arne.arne.R
import com.tricycle_sec.arne.arne.base.BaseActivity
import com.tricycle_sec.arne.arne.login.LoginActivity

class NavigationDrawerHandler {

    fun handleNavigationItem(item: MenuItem, activity: BaseActivity) {

        when (item.itemId) {
            R.id.nav_menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                activity.startActivity(Intent(activity, LoginActivity::class.java))
            }
            else -> {
            }
        }
    }
}
