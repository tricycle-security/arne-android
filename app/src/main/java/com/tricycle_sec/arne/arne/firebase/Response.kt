package com.tricycle_sec.arne.arne.firebase

import java.io.Serializable

class Response(val responding: Boolean = false, val time: Long = 0, val uuid: String = "") : Serializable
