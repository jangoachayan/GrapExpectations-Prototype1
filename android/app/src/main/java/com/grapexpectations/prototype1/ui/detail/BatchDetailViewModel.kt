package com.grapexpectations.prototype1.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grapexpectations.prototype1.data.model.Batch
import com.grapexpectations.prototype1.data.repository.BatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BatchDetailViewModel(private val batchId: String) : ViewModel() {
    private val repository = BatchRepository()

    private val _batch = MutableStateFlow<Batch?>(null)
    val batch: StateFlow<Batch?> = _batch

    private val _readings = MutableStateFlow<List<com.grapexpectations.prototype1.data.model.ReadingPoint>>(emptyList())
    val readings: StateFlow<List<com.grapexpectations.prototype1.data.model.ReadingPoint>> = _readings

    init {
        viewModelScope.launch {
            repository.getBatch(batchId).collect {
                _batch.value = it
            }
        }
        
        viewModelScope.launch {
            repository.getReadings(batchId).collect {
                _readings.value = it
            }
        }
    }

}
