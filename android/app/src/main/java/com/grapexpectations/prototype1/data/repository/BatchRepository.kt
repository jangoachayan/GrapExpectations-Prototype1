package com.grapexpectations.prototype1.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.grapexpectations.prototype1.data.model.Batch
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
}
