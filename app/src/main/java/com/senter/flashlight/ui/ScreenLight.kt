package com.senter.flashlight.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Mode senter berbasis layar: putih, kecerahan 100% — bersifat per-window via
 * WindowManager.LayoutParams.screenBrightness, sehingga TIDAK butuh izin WRITE_SETTINGS.
 */
@Composable
fun ScreenLight(onExit: () -> Unit) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        val original = window?.attributes?.screenBrightness
        window?.let {
            val lp = it.attributes
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL // 1.0f
            it.attributes = lp
            it.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.let {
                val lp = it.attributes
                lp.screenBrightness =
                    original ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE // -1f: ikut sistem
                it.attributes = lp
                it.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onExit() },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            "Ketuk untuk keluar",
            color = Color(0xFF888888),
            modifier = Modifier.padding(40.dp),
        )
    }
}
