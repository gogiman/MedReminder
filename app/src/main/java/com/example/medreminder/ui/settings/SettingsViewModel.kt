package com.example.medreminder.ui.settings

import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.medreminder.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val soundUri: Uri = SettingsRepository.DEFAULT_SOUND_URI,
    val soundName: String = "Default notification sound"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val ringtoneManagerWrapper: RingtoneManagerWrapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val uri = settingsRepository.getSoundUri()
        _uiState.update { it.copy(soundUri = uri, soundName = getSoundName(uri)) }
    }

    fun onSoundSelected(uri: Uri?) {
        val resolvedUri = uri ?: SettingsRepository.DEFAULT_SOUND_URI
        settingsRepository.setSoundUri(resolvedUri)
        _uiState.update { it.copy(soundUri = resolvedUri, soundName = getSoundName(resolvedUri)) }
    }

    fun onSilentSelected() {
        settingsRepository.setSoundUri(null)
        _uiState.update { it.copy(soundUri = Uri.EMPTY, soundName = "Silent") }
    }

    private fun getSoundName(uri: Uri): String {
        if (uri == Uri.EMPTY) return "Silent"
        return runCatching {
            ringtoneManagerWrapper.getRingtoneName(uri)
        }.getOrElse { uri.lastPathSegment ?: "Unknown" }
    }
}
