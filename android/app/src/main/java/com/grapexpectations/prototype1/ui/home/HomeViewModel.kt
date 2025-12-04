package com.grapexpectations.prototype1.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grapexpectations.prototype1.data.model.Batch
import com.grapexpectations.prototype1.data.repository.BatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = BatchRepository()

    private val _batches = MutableStateFlow<List<Batch>>(emptyList())
    val batches: StateFlow<List<Batch>> = _batches

    init {
        viewModelScope.launch {
            repository.getBatches().collect {
                _batches.value = it
            }
        }
    }
    
    fun addSampleBatch() {
        val batch = Batch(
            name = "Test Batch ${System.currentTimeMillis()}",
            status = "active",
            startAt = java.util.Date()
        )
        repository.addBatch(batch)
    }
}
