package com.grapexpectations.prototype1.data.model

import java.util.Date

data class ReadingPoint(
    val timestamp: Date,
    val sg: Double? = null,
    val tempC: Double? = null
)
