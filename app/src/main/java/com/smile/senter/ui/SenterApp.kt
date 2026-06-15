package com.smile.senter.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smile.senter.R
import com.smile.senter.SosController
import com.smile.senter.TorchController
import com.smile.senter.TorchState
import com.smile.senter.data.SettingsRepository
import com.smile.senter.ui.theme.SenterTheme
import kotlinx.coroutines.launch

/**
 * Konfigurasi demo-mode (HANYA dipakai di build debug) untuk merender state ideal
 * saat pengambilan screenshot Play Store, tanpa perlu hardware flash nyata.
 */
data class DemoConfig(
    val torchOn: Boolean = false,
    val sos: Boolean = false,
    val screenLight: Boolean = false,
    val dark: Boolean? = null,   // null = ikut tema sistem
)

@Composable
fun SenterApp(
    torch: TorchController,
    sos: SosController,
    settings: SettingsRepository,
    onAutoOffChanged: (Boolean) -> Unit,
    demo: DemoConfig? = null,
) {
    val darkOverride by settings.darkThemeOverride.collectAsStateWithLifecycle(initialValue = null)
    val autoOff by settings.autoOffOnExit.collectAsStateWithLifecycle(initialValue = true)
    LaunchedEffect(autoOff) { onAutoOffChanged(autoOff) }

    SenterTheme(darkTheme = demo?.dark ?: (darkOverride ?: isSystemInDarkTheme())) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            SenterScreen(
                torch = torch,
                sos = sos,
                darkOverride = darkOverride,
                autoOff = autoOff,
                settings = settings,
                initialSos = demo?.sos == true,
                initialScreenLight = demo?.screenLight == true,
            )
        }
    }
}

@Composable
private fun SenterScreen(
    torch: TorchController,
    sos: SosController,
    darkOverride: Boolean?,
    autoOff: Boolean,
    settings: SettingsRepository,
    initialSos: Boolean = false,
    initialScreenLight: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val torchState by torch.state.collectAsStateWithLifecycle()
    var sosRunning by remember { mutableStateOf(initialSos) }
    var screenLight by remember { mutableStateOf(initialScreenLight) }

    if (screenLight) {
        ScreenLight(onExit = { screenLight = false })
        return
    }

    val isOn = torchState is TorchState.On
    val statusText = when (val s = torchState) {
        TorchState.On -> stringResource(R.string.status_on)
        TorchState.Off -> stringResource(R.string.status_off)
        TorchState.Unavailable -> stringResource(R.string.status_unavailable)
        is TorchState.Error -> s.reason
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = {
                val next = !(darkOverride ?: false)
                scope.launch { settings.setDarkTheme(next) }
            }) {
                Text(stringResource(if (darkOverride == true) R.string.theme_light else R.string.theme_dark))
            }
        }

        Spacer(Modifier.height(48.dp))
        Text(statusText, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (sosRunning) { sos.stop(); sosRunning = false }
                torch.toggle()
            },
            enabled = torch.isAvailable,
            modifier = Modifier.size(190.dp),
            colors = ButtonDefaults.buttonColors(),
        ) {
            Text(
                stringResource(if (isOn) R.string.btn_turn_off else R.string.btn_turn_on),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Spacer(Modifier.height(36.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = {
                    if (sosRunning) {
                        sos.stop(); sosRunning = false
                    } else {
                        sos.start(); sosRunning = true
                    }
                },
                enabled = torch.isAvailable,
            ) { Text(stringResource(if (sosRunning) R.string.btn_sos_stop else R.string.btn_sos)) }

            OutlinedButton(onClick = { screenLight = true }) { Text(stringResource(R.string.btn_screen_light)) }
        }

        Spacer(Modifier.height(48.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.auto_off_label))
            Switch(
                checked = autoOff,
                onCheckedChange = { scope.launch { settings.setAutoOff(it) } },
            )
        }
    }
}
