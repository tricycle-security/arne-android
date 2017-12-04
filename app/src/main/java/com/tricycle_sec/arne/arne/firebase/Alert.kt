package com.tricycle_sec.arne.arne.firebase
class Alert(val active: Boolean = false, val description: String = "" , val id: String = "", val kind: String = "", val location: String = "", val time: Long = 0, val responses: MutableList<Response> = mutableListOf(Response(false, 0, "")))