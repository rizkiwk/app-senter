package com.smile.senter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Strobe pola Morse "SOS" ( · · ·  — — —  · · · ) memakai [TorchController].
 * Tanpa izin apa pun — hanya memanggil setTorchMode berulang dari coroutine.
 */
class SosController(
    private val torch: TorchController,
    private val scope: CoroutineScope,
) {
    private var job: Job? = null
    val isRunning: Boolean get() = job?.isActive == true

    private val unit = 220L // durasi satu "dot" dalam milidetik

    fun start() {
        if (isRunning) return
        job = scope.launch(Dispatchers.Default) {
            val dot = unit
            val dash = unit * 3
            val symbolGap = unit       // jeda antar simbol dalam satu huruf
            val letterGap = unit * 3   // jeda antar huruf
            val wordGap = unit * 7     // jeda sebelum mengulang
            // S=· · ·  O=— — —  S=· · · ; batas huruf setelah indeks 2 dan 5.
            val pattern = listOf(dot, dot, dot, dash, dash, dash, dot, dot, dot)
            while (isActive) {
                pattern.forEachIndexed { i, on ->
                    torch.setTorch(true); delay(on)
                    torch.setTorch(false)
                    delay(if (i == 2 || i == 5) letterGap else symbolGap)
                }
                delay(wordGap)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        torch.setTorch(false)
    }
}
