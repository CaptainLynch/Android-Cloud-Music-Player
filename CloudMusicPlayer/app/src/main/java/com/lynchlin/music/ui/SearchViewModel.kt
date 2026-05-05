package com.lynchlin.music.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lynchlin.music.data.model.Song
import com.lynchlin.music.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults: StateFlow<List<Song>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun searchMusic(keyword: String) {
        if (keyword.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = RetrofitClient.apiService.searchMusic(keyword = keyword)
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch data"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
