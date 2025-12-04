package com.grapexpectations.prototype1.data.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Batch(
    var id: String = "",
    val orgId: String = "",
    val recipeId: String = "",
    val name: String = "", // Added for display convenience, though spec implies it might be in recipe
    val startAt: Date? = null,
    val endAt: Date? = null,
    val status: String = "planned", // planned, active, conditioning, bottled
    val sensors: Map<String, String> = emptyMap(),
    val summary: BatchSummary = BatchSummary()
)

data class BatchSummary(
    val og: Double = 0.0,
    val fg: Double = 0.0,
    val abv: Double = 0.0,
    val peakTemp: Double = 0.0,
    val minTemp: Double = 0.0
)
