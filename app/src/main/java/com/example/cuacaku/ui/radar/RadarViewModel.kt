package com.example.cuacaku.ui.radar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuacaku.data.WeatherRepository
import com.example.cuacaku.data.remote.RadarFrame
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// =====================================================
// Item frame radar dengan waktu terformat
// =====================================================
data class RadarFrameItem(
    val time: Long,
    val path: String,
    val timeFormatted: String,
    val isNowcast: Boolean
)

// =====================================================
// UI State untuk layar Radar Cuaca
// =====================================================
sealed interface RadarUiState {
    data object Loading : RadarUiState
    data class Error(val message: String) : RadarUiState
    data class Success(
        val host: String,
        val frames: List<RadarFrameItem>,
        val currentFrameIndex: Int,
        val isPlaying: Boolean
    ) : RadarUiState
}

// =====================================================
// RadarViewModel — mengelola data frame radar dan playback
// =====================================================
class RadarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow<RadarUiState>(RadarUiState.Loading)
    val uiState: StateFlow<RadarUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    init {
        loadRadar()
    }

    /**
     * Mengambil metadata radar dari RainViewer API
     */
    fun loadRadar() {
        viewModelScope.launch {
            _uiState.value = RadarUiState.Loading
            stopPlayback()

            val response = repository.radarMaps()
            if (response == null || response.radar == null) {
                _uiState.value = RadarUiState.Error("Gagal memuat data radar cuaca. Periksa koneksi internet.")
                return@launch
            }

            val host = response.host ?: "https://tilecache.rainviewer.com"
            val pastFrames = response.radar.past ?: emptyList()
            val nowcastFrames = response.radar.nowcast ?: emptyList()

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val allFrames = mutableListOf<RadarFrameItem>()
            pastFrames.forEach { frame ->
                allFrames.add(
                    RadarFrameItem(
                        time = frame.time,
                        path = frame.path,
                        timeFormatted = timeFormat.format(Date(frame.time * 1000L)),
                        isNowcast = false
                    )
                )
            }
            nowcastFrames.forEach { frame ->
                allFrames.add(
                    RadarFrameItem(
                        time = frame.time,
                        path = frame.path,
                        timeFormatted = timeFormat.format(Date(frame.time * 1000L)),
                        isNowcast = true
                    )
                )
            }

            if (allFrames.isEmpty()) {
                _uiState.value = RadarUiState.Error("Tidak ada frame radar aktif saat ini.")
            } else {
                // Default ke frame masa lampau terakhir (paling terkini)
                val defaultIndex = if (pastFrames.isNotEmpty()) pastFrames.lastIndex else allFrames.lastIndex
                _uiState.value = RadarUiState.Success(
                    host = host,
                    frames = allFrames,
                    currentFrameIndex = defaultIndex,
                    isPlaying = false
                )
            }
        }
    }

    /**
     * Memilih frame radar tertentu (misal dari slider linimasa)
     */
    fun selectFrame(index: Int) {
        val state = _uiState.value as? RadarUiState.Success ?: return
        if (index in state.frames.indices) {
            _uiState.value = state.copy(currentFrameIndex = index)
        }
    }

    /**
     * Play/Pause animasi pergerakan awan hujan
     */
    fun togglePlay() {
        val state = _uiState.value as? RadarUiState.Success ?: return
        if (state.isPlaying) {
            stopPlayback()
            _uiState.value = state.copy(isPlaying = false)
        } else {
            _uiState.value = state.copy(isPlaying = true)
            startPlayback()
        }
    }

    private fun startPlayback() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (isActive) {
                delay(850)
                val currentState = _uiState.value as? RadarUiState.Success ?: break
                if (!currentState.isPlaying) break

                val nextIndex = (currentState.currentFrameIndex + 1) % currentState.frames.size
                _uiState.value = currentState.copy(currentFrameIndex = nextIndex)
            }
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }
}
