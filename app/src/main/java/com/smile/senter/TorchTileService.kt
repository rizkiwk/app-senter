package com.smile.senter

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

/**
 * Quick Settings Tile untuk men-toggle senter langsung dari panel pengaturan cepat (API 24+).
 * BIND_QUICK_SETTINGS_TILE adalah signature permission yang ditegakkan sistem saat binding —
 * bukan izin yang diminta dari user, sehingga klaim "nol izin user" tetap utuh.
 */
@RequiresApi(Build.VERSION_CODES.N)
class TorchTileService : TileService() {

    private val torch: TorchController by lazy { TorchController(applicationContext) }

    override fun onStartListening() {
        torch.register()
        syncTile()
    }

    override fun onStopListening() {
        torch.unregister()
    }

    override fun onClick() {
        if (!torch.isAvailable) return
        torch.toggle()
        syncTile()
    }

    private fun syncTile() {
        val tile = qsTile ?: return
        tile.state = when {
            !torch.isAvailable -> Tile.STATE_UNAVAILABLE
            torch.state.value is TorchState.On -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }
}
