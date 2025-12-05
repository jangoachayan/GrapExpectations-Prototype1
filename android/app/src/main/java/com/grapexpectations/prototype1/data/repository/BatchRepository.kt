package com.grapexpectations.prototype1.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.grapexpectations.prototype1.data.model.Batch
import com.grapexpectations.prototype1.data.model.ReadingPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BatchRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val batchesCollection = firestore.collection("batches")

    // Fetch all batches for a specific organization (or user for MVP)
    fun getBatches(): Flow<List<Batch>> = callbackFlow {
        // For MVP, we might just query all batches or filter by a hardcoded org/user if we had one.
        // Assuming we want to show all batches for now or filter by 'status' if needed.
        val subscription = batchesCollection
            .orderBy("startAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val batches = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Batch::class.java)?.apply { id = doc.id }
                    }
                    trySend(batches)
                }
            }

        awaitClose { subscription.remove() }
    }

    // Add a new batch
    fun addBatch(batch: Batch) {
        batchesCollection.add(batch)
    }

    // Get a single batch by ID
    fun getBatch(batchId: String): Flow<Batch?> = callbackFlow {
        val subscription = batchesCollection.document(batchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val batch = snapshot.toObject(Batch::class.java)?.apply { id = snapshot.id }
                    trySend(batch)
                } else {
                    trySend(null)
                }
            }
        awaitClose { subscription.remove() }
    }

    // Get readings for a batch (aggregating from daily documents)
    fun getReadings(batchId: String): Flow<List<ReadingPoint>> = callbackFlow {
        val subscription = batchesCollection.document(batchId)
            .collection("readings_daily")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val allPoints = mutableListOf<ReadingPoint>()
                    for (doc in snapshot.documents) {
                        // Assuming 'points' field is a list of maps/objects
                        val points = doc.get("points") as? List<Map<String, Any>>
                        points?.forEach { p ->
                            // Parse map to ReadingPoint
                            val t = (p["t"] as? com.google.firebase.Timestamp)?.toDate()
                            val sg = (p["sg"] as? Number)?.toDouble()
                            val temp = (p["tempC"] as? Number)?.toDouble()
                            
                            if (t != null) {
                                allPoints.add(ReadingPoint(t, sg, temp))
                            }
                        }
                    }
                    // Sort by time
                    allPoints.sortBy { it.timestamp }
                    trySend(allPoints)
                }
            }
        awaitClose { subscription.remove() }
    }
}

