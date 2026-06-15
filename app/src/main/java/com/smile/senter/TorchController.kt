package com.smile.senter

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** State torch. Sumber kebenaran berasal dari hardware lewat [CameraManager.TorchCallback]. */
sealed interface TorchState {
    data object Off : TorchState
    data object On : TorchState
    data object Unavailable : TorchState
    data class Error(val reason: String) : TorchState
}

/**
 * Pengendali senter berbasis [CameraManager.setTorchMode] — TANPA izin CAMERA,
 * tanpa membuka sesi kamera. Tersedia sejak API 23.
 */
@RequiresApi(Build.VERSION_CODES.M) // API 23
class TorchController(context: Context, private val demoMode: Boolean = false) {

    private val appContext = context.applicationContext

    private val cameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    // Demo-mode (debug) melewati resolusi hardware agar UI bisa dirender di emulator tanpa flash.
    private val torchCameraId: String? = if (demoMode) null else resolveTorchCameraId()

    private val _state = MutableStateFlow<TorchState>(
        if (!demoMode && torchCameraId == null) TorchState.Unavailable else TorchState.Off
    )
    val state: StateFlow<TorchState> = _state.asStateFlow()

    val isAvailable: Boolean get() = demoMode || torchCameraId != null

    /** Jumlah level kecerahan torch; > 1 berarti kontrol kecerahan didukung (API 33+). */
    val maxStrengthLevel: Int = resolveMaxStrengthLevel()
    val supportsStrength: Boolean get() = maxStrengthLevel > 1

    // Overload (TorchCallback, Handler) WAJIB dipakai: overload (Executor, ..) baru ada di API 28,
    // sedangkan minSdk = 23.
    private val mainHandler = Handler(Looper.getMainLooper())

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId == torchCameraId) {
                _state.value = if (enabled) TorchState.On else TorchState.Off
            }
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            if (cameraId == torchCameraId) {
                _state.value = TorchState.Error(appContext.getString(R.string.err_torch_in_use))
            }
        }
    }

    fun register() {
        if (demoMode) return
        cameraManager.registerTorchCallback(torchCallback, mainHandler)
    }

    fun unregister() {
        if (demoMode) return
        runCatching { cameraManager.unregisterTorchCallback(torchCallback) }
    }

    /** Pilih cameraId pertama yang punya flash; prioritaskan kamera belakang. */
    private fun resolveTorchCameraId(): String? = try {
        val ids = cameraManager.cameraIdList
        ids.firstOrNull { id ->
            val c = cameraManager.getCameraCharacteristics(id)
            c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        } ?: ids.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    } catch (e: CameraAccessException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }

    private fun resolveMaxStrengthLevel(): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return 1
        val id = torchCameraId ?: return 1
        return try {
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL) ?: 1
        } catch (e: Exception) {
            1
        }
    }

    /** Nyala/mati torch. State otoritatif tetap datang dari callback. */
    fun setTorch(on: Boolean): Boolean {
        if (demoMode) {
            _state.value = if (on) TorchState.On else TorchState.Off
            return true
        }
        val id = torchCameraId ?: run {
            _state.value = TorchState.Unavailable
            return false
        }
        return try {
            cameraManager.setTorchMode(id, on)
            true
        } catch (e: CameraAccessException) {
            _state.value = TorchState.Error(mapCameraError(e))
            false
        } catch (e: IllegalArgumentException) {
            _state.value = TorchState.Error(appContext.getString(R.string.err_torch_unavailable))
            false
        }
    }

    /** Nyalakan torch pada level kecerahan tertentu (1..[maxStrengthLevel]), API 33+. */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setStrength(level: Int): Boolean {
        val id = torchCameraId ?: return false
        if (maxStrengthLevel <= 1) return setTorch(true)
        val clamped = level.coerceIn(1, maxStrengthLevel)
        return try {
            cameraManager.turnOnTorchWithStrengthLevel(id, clamped)
            true
        } catch (e: CameraAccessException) {
            _state.value = TorchState.Error(mapCameraError(e))
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun toggle(): Boolean = setTorch(_state.value !is TorchState.On)

    private fun mapCameraError(e: CameraAccessException): String = when (e.reason) {
        CameraAccessException.CAMERA_IN_USE,
        CameraAccessException.MAX_CAMERAS_IN_USE -> appContext.getString(R.string.err_camera_in_use)
        CameraAccessException.CAMERA_DISABLED -> appContext.getString(R.string.err_camera_disabled)
        CameraAccessException.CAMERA_DISCONNECTED -> appContext.getString(R.string.err_camera_disconnected)
        else -> appContext.getString(R.string.err_camera_generic)
    }
}
