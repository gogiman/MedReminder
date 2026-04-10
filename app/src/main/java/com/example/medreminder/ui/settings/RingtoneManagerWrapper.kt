package com.example.medreminder.ui.settings

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RingtoneManagerWrapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getRingtoneName(uri: Uri): String {
        return RingtoneManager.getRingtone(context, uri)?.getTitle(context) ?: "Unknown"
    }
}
