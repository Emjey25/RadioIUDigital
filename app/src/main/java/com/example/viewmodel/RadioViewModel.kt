package com.example.viewmodel

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PodcastProgram
import com.example.data.RadioNotification
import com.example.data.RadioRepository
import com.example.data.RadioStation
import com.example.data.StationCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StationSource(val label: String) {
    ALL("Todas"),
    INSTITUTIONAL("IU Digital"),
    RADIO_BROWSER("Radio-Browser API")
}

data class RadioUiState(
    val stations: List<RadioStation> = RadioRepository.initialStations,
    val apiStations: List<RadioStation> = emptyList(),
    val activeStation: RadioStation = RadioRepository.initialStations.first(),
    val isPlaying: Boolean = true,
    val isMuted: Boolean = false,
    val elapsedSeconds: Int = 42 * 60 + 18,
    val volume: Float = 0.85f,
    val selectedCategory: StationCategory = StationCategory.ALL,
    val selectedSource: StationSource = StationSource.ALL,
    val favoriteStationIds: Set<String> = setOf("iu_stereo"),
    val podcasts: List<PodcastProgram> = RadioRepository.samplePodcasts,
    val notifications: List<RadioNotification> = RadioRepository.sampleNotifications,
    val unreadNotificationsCount: Int = 2,
    val currentTab: Int = 0,
    val showFilterSheet: Boolean = false,
    val showCastSheet: Boolean = false,
    val showNotificationsSheet: Boolean = false,
    val showPhotoCaptureDialog: Boolean = false,
    val studentName: String = "Estudiante IU Digital",
    val studentProgram: String = "Ingeniería de Software",
    val studentSemester: String = "Semestre 2025-1",
    val customPhotoUri: String? = null,
    val snackbarMessage: String? = null,
    val connectedCastDevice: String? = null,
    val isApiLoading: Boolean = false,
    val apiError: String? = null,
    val apiSearchQuery: String = ""
)

class RadioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RadioUiState())
    val uiState: StateFlow<RadioUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var searchJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    init {
        startTimer()
        loadLiveTopStations()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.isPlaying) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun loadLiveTopStations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isApiLoading = true, apiError = null) }
            val result = RadioRepository.fetchTopStations(limit = 25)
            result.onSuccess { apiList ->
                _uiState.update { current ->
                    val combined = RadioRepository.initialStations + apiList.filter { api ->
                        RadioRepository.initialStations.none { it.id == api.id }
                    }
                    current.copy(
                        apiStations = apiList,
                        stations = combined,
                        isApiLoading = false,
                        apiError = if (apiList.isEmpty()) "No se encontraron emisoras en vivo" else null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isApiLoading = false,
                        apiError = "Error al conectar con Radio-Browser API: ${err.localizedMessage}"
                    )
                }
            }
        }
    }

    fun searchRadioBrowser(query: String) {
        _uiState.update { it.copy(apiSearchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // Debounce
            if (query.trim().isEmpty()) {
                loadLiveTopStations()
                return@launch
            }
            _uiState.update { it.copy(isApiLoading = true, apiError = null) }
            val result = RadioRepository.searchStations(query = query, limit = 30)
            result.onSuccess { results ->
                _uiState.update { current ->
                    val combined = RadioRepository.initialStations + results.filter { api ->
                        RadioRepository.initialStations.none { it.id == api.id }
                    }
                    current.copy(
                        apiStations = results,
                        stations = combined,
                        isApiLoading = false,
                        apiError = if (results.isEmpty()) "No se encontraron resultados para '$query'" else null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isApiLoading = false,
                        apiError = "Error al buscar en Radio-Browser API: ${err.localizedMessage}"
                    )
                }
            }
        }
    }

    fun setSourceFilter(source: StationSource) {
        _uiState.update { it.copy(selectedSource = source) }
    }

    fun selectStation(station: RadioStation) {
        _uiState.update {
            it.copy(
                activeStation = station,
                isPlaying = true,
                elapsedSeconds = 0,
                snackbarMessage = "Sintonizando: ${station.name}"
            )
        }
        if (station.streamUrl.isNotBlank()) {
            startStreamPlayback(station.streamUrl)
        } else {
            stopStreamPlayback()
        }
    }

    fun togglePlayPause() {
        _uiState.update {
            val nextState = !it.isPlaying
            if (!nextState) {
                pauseStreamPlayback()
            } else if (it.activeStation.streamUrl.isNotBlank()) {
                resumeOrStartStreamPlayback(it.activeStation.streamUrl)
            }
            it.copy(
                isPlaying = nextState,
                snackbarMessage = if (nextState) "Emisión reanudada" else "Emisión pausada"
            )
        }
    }

    private fun startStreamPlayback(streamUrl: String) {
        try {
            stopStreamPlayback()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(streamUrl)
                setOnPreparedListener { mp ->
                    if (_uiState.value.isPlaying) {
                        mp.start()
                    }
                }
                setOnErrorListener { _, _, _ ->
                    _uiState.update {
                        it.copy(
                            snackbarMessage = "Audio no disponible temporalmente en este stream"
                        )
                    }
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            // Ignore stream initialization failure in background or mock tests
        }
    }

    private fun pauseStreamPlayback() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    private fun resumeOrStartStreamPlayback(streamUrl: String) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer?.start()
            } else {
                startStreamPlayback(streamUrl)
            }
        } catch (e: Exception) {
            startStreamPlayback(streamUrl)
        }
    }

    private fun stopStreamPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            mediaPlayer = null
        }
    }

    fun toggleMute() {
        _uiState.update {
            val nextMuted = !it.isMuted
            val vol = if (nextMuted) 0f else it.volume
            try {
                mediaPlayer?.setVolume(vol, vol)
            } catch (e: Exception) {}
            it.copy(
                isMuted = nextMuted,
                snackbarMessage = if (nextMuted) "Audio silenciado" else "Audio activado"
            )
        }
    }

    fun skipBackward(seconds: Int = 10) {
        _uiState.update {
            val newTime = (it.elapsedSeconds - seconds).coerceAtLeast(0)
            it.copy(
                elapsedSeconds = newTime,
                snackbarMessage = "Retroceso de $seconds s"
            )
        }
    }

    fun skipForward(seconds: Int = 10) {
        _uiState.update {
            it.copy(
                elapsedSeconds = it.elapsedSeconds + seconds,
                snackbarMessage = "Avance de $seconds s"
            )
        }
    }

    fun toggleFavorite(stationId: String) {
        _uiState.update { current ->
            val updated = if (current.favoriteStationIds.contains(stationId)) {
                current.favoriteStationIds - stationId
            } else {
                current.favoriteStationIds + stationId
            }
            val isNowFav = updated.contains(stationId)
            current.copy(
                favoriteStationIds = updated,
                snackbarMessage = if (isNowFav) "Añadido a favoritas" else "Eliminado de favoritas"
            )
        }
    }

    fun setCategoryFilter(category: StationCategory) {
        _uiState.update { it.copy(selectedCategory = category, showFilterSheet = false) }
    }

    fun setCurrentTab(index: Int) {
        _uiState.update { it.copy(currentTab = index) }
    }

    fun setFilterSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showFilterSheet = visible) }
    }

    fun setCastSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showCastSheet = visible) }
    }

    fun setNotificationsSheetVisible(visible: Boolean) {
        _uiState.update {
            it.copy(
                showNotificationsSheet = visible,
                unreadNotificationsCount = if (visible) 0 else it.unreadNotificationsCount
            )
        }
    }

    fun setPhotoCaptureDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showPhotoCaptureDialog = visible) }
    }

    fun updateStudentPhoto(uriString: String?) {
        _uiState.update {
            it.copy(
                customPhotoUri = uriString,
                showPhotoCaptureDialog = false,
                snackbarMessage = "Foto de perfil actualizada con éxito"
            )
        }
    }

    fun connectCastDevice(deviceName: String) {
        _uiState.update {
            it.copy(
                connectedCastDevice = deviceName,
                showCastSheet = false,
                snackbarMessage = "Transmitiendo a: $deviceName"
            )
        }
    }

    fun disconnectCast() {
        _uiState.update {
            it.copy(
                connectedCastDevice = null,
                snackbarMessage = "Transmisión local en dispositivo"
            )
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun formatElapsedTime(totalSeconds: Int): String {
        val minutes = (totalSeconds / 60) % 100
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        searchJob?.cancel()
        stopStreamPlayback()
    }
}
