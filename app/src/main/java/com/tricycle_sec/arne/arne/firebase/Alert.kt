package com.tricycle_sec.arne.arne.firebase

import java.io.Serializable

class Alert(val active: Boolean = false, val description: String = "" , val id: String = "", val kind: String = "", val location: String = "", val time: Long = 0, val responders: Map<String, Response> = mapOf()) : Serializable