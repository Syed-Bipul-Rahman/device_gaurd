package me.bipul.devicegaurd

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val lastUsed: Long,
    val totalTimeInForeground: Long
)