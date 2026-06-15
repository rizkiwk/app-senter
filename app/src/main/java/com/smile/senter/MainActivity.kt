package com.smile.senter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.smile.senter.BuildConfig
import com.smile.senter.data.SettingsRepository
import com.smile.senter.ui.DemoConfig
import com.smile.senter.ui.SenterApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var torch: TorchController
    private lateinit var sos: SosController
    private lateinit var settings: SettingsRepository

    // Disinkronkan dari DataStore lewat UI; menentukan perilaku saat app ke background.
    @Volatile private var autoOff = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash sistem via androidx core-splashscreen — backport mulus ke API 23.
        // WAJIB dipanggil sebelum super.onCreate() agar tema splash terpasang.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Tahan splash sejenak agar brand moment terlihat, lalu animasikan keluar
        // (ikon petir membesar + memudar). App ini ringan, jadi tahanannya singkat.
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        lifecycleScope.launch {
            delay(SPLASH_HOLD_MS)
            keepSplashOnScreen = false
        }
        splashScreen.setOnExitAnimationListener { screen ->
            screen.iconView.animate()
                .alpha(0f)
                .scaleX(1.4f)
                .scaleY(1.4f)
                .setDuration(SPLASH_EXIT_MS)
                .withEndAction { screen.remove() }
                .start()
        }

        enableEdgeToEdge()

        // Demo-mode HANYA di build debug: render state ideal untuk screenshot Play Store
        // tanpa hardware flash. Di release BuildConfig.DEBUG=false → selalu null (R8 buang).
        val demo = if (BuildConfig.DEBUG) parseDemoConfig() else null

        torch = TorchController(applicationContext, demoMode = demo != null)
        sos = SosController(torch, lifecycleScope)
        settings = SettingsRepository(applicationContext)
        if (demo?.torchOn == true) torch.setTorch(true)
        setContent {
            SenterApp(
                torch = torch,
                sos = sos,
                settings = settings,
                onAutoOffChanged = { autoOff = it },
                demo = demo,
            )
        }
    }

    /** Baca intent extras demo (debug saja). Contoh: `--ez demo true --es scene on --es theme dark`. */
    private fun parseDemoConfig(): DemoConfig? {
        if (!intent.getBooleanExtra("demo", false)) return null
        val scene = intent.getStringExtra("scene") ?: "on"
        val theme = intent.getStringExtra("theme")
        return DemoConfig(
            torchOn = scene == "on" || scene == "sos",
            sos = scene == "sos",
            screenLight = scene == "screenlight",
            dark = when (theme) {
                "dark" -> true
                "light" -> false
                else -> null
            },
        )
    }

    override fun onResume() {
        super.onResume()
        torch.register() // re-sync state nyata via TorchCallback
    }

    override fun onPause() {
        torch.unregister()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        // Profil paling aman & hemat baterai: matikan senter saat app benar-benar
        // ke background, kecuali user menonaktifkan opsi ini.
        if (autoOff) {
            sos.stop()
            torch.setTorch(false)
        }
    }

    private companion object {
        const val SPLASH_HOLD_MS = 700L   // durasi brand moment splash
        const val SPLASH_EXIT_MS = 350L   // durasi animasi keluar ikon
    }
}
