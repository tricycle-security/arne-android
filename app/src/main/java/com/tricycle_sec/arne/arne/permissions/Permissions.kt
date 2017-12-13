package com.tricycle_sec.arne.arne.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.greysonparrelli.permiso.Permiso

class Permissions {
    companion object {
        fun request(permission: String, permissionGranted: (result: Boolean) -> Unit,
                    rationaleTitle: String? = null, rationaleMessage: String? = null, buttonText: String? = null) {
            Permiso.getInstance().requestPermissions(object : Permiso.IOnPermissionResult {
                override fun onRationaleRequested(callback: Permiso.IOnRationaleProvided, vararg permissions: String?) {

                    if (rationaleTitle != null && rationaleMessage != null) {
                        Permiso.getInstance().showRationaleInDialog(rationaleTitle, rationaleMessage, buttonText, callback)
                        return
                    }

                    callback.onRationaleProvided()
                }

                override fun onPermissionResult(resultSet: Permiso.ResultSet) {
                    permissionGranted(resultSet.areAllPermissionsGranted())
                }
            }, permission)
        }

        fun isGranted(context: Context, permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}