package com.danielmijens.loginapp

import com.google.type.DateTime
import java.io.Serializable
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

data class Mensaje(val emisor : String ?= null,
                   val mensaje : String ?= null,
                   val hora : Calendar?= null) : Serializable