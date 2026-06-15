# Senter 🔦

Aplikasi senter Android **tanpa izin (zero-permission)**, **offline**, tanpa iklan, tanpa
analitik. Dibangun dengan prinsip _Kingdom of Science_: senter = `CameraManager` + 1
cameraId ber-flash + 1 bit.

## Kenapa nol izin?
`CameraManager.setTorchMode()` (API 23+) mengontrol LED flash **tanpa membuka sesi kamera**
dan **tanpa membaca sensor gambar**, sehingga **tidak memerlukan** izin `CAMERA`. Manifest
tidak memuat satu pun `<uses-permission>`.

## Stack
| Komponen | Versi |
|---|---|
| Gradle | 8.13 (wrapper) |
| Android Gradle Plugin | 8.13.1 |
| Kotlin / Compose Compiler | 2.0.21 |
| Compose BOM | 2024.09.03 (Material3) |
| minSdk / targetSdk / compileSdk | 23 / 36 / 36 |
| Penyimpanan | DataStore Preferences (bukan SQLite/Room) |

## Fitur
- Toggle senter (ON/OFF) dengan state otoritatif dari hardware (`TorchCallback`)
- SOS strobe pola Morse
- Mode senter layar (putih 100%, per-window, tanpa `WRITE_SETTINGS`)
- Quick Settings Tile (API 24+) & App Shortcut
- Mode gelap/terang + "matikan otomatis saat keluar"
- Penanganan: device tanpa flash, kamera dipakai app lain, rotasi, background

## Build
```bash
# Debug APK
./gradlew :app:assembleDebug

# Release App Bundle (AAB) ter-sign untuk Play
./gradlew :app:bundleRelease
# → app/build/outputs/bundle/release/app-release.aab (~1.9 MB)
```
SDK path di-set lewat `local.properties` (tidak di-commit).

## Signing
`app/build.gradle.kts` membaca kredensial dari `~/keystores/senter-keystore.properties`
(**di luar repo**, `chmod 600`). Jika file tidak ada, build tetap jalan tanpa signing
(aman untuk CI/mesin lain).

| Item | Nilai |
|---|---|
| Keystore | `~/keystores/senter-upload.jks` |
| Alias | `upload` (RSA 2048) |
| Validity | s/d 20 Mei 2126 (~100 thn) |
| Peran | Upload key (aktifkan **Play App Signing** saat upload perdana) |

Format `senter-keystore.properties`:
```properties
storeFile=/path/ke/senter-upload.jks
storePassword=...
keyAlias=upload
keyPassword=...
```

> 🔑 **Backup** keystore + properties ke password manager / penyimpanan terenkripsi.
> Hilang = tidak bisa update app (kecuali reset upload key via Play App Signing).

## Struktur
```
app/src/main/java/com/senter/flashlight/
├── MainActivity.kt          # host Compose + siklus hidup torch
├── TorchController.kt        # inti setTorchMode + TorchCallback + TorchState
├── SosController.kt          # strobe Morse SOS (coroutine)
├── TorchTileService.kt       # Quick Settings Tile
├── data/SettingsRepository.kt# DataStore (preferensi lokal)
└── ui/                       # SenterApp, ScreenLight, theme/
```

## Checklist Rilis ke Google Play (akun Organisasi terverifikasi)
1. ~~**Build AAB ter-sign**~~ ✅ — `./gradlew :app:bundleRelease` (lihat bagian [Signing](#signing); keystore validity ~100 thn, di luar repo).
2. **Privacy Policy** — host `PRIVACY_POLICY.md` ke URL publik (GitHub Pages/Notion).
3. **Play Console** → buat app → Store listing (screenshot = layar app ASLI, ikon orisinal, nama tidak spammy).
4. **Data Safety** → "No data collected / No data shared" + URL Privacy Policy.
5. **Content rating** (IARC) → kuesioner jujur → Everyone.
6. **Upload AAB** → Production (atau Internal testing 1 putaran lalu promote).
7. **Tunggu review:** ~24–72 jam (akun Organisasi melewati gate closed-testing 12-tester/14-hari).

> ⚠️ **Jangan** menambahkan SDK iklan/analitik/Firebase — begitu masuk, `AD_ID` ter-merge
> dan Data Safety berubah jadi "data collected", merusak profil review terbersih.
