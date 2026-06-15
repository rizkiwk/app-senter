# 🔦 Saran Tim — Kingdom of Science Multi-Agent AI Core

> Dokumen advisory untuk proyek **Senter** (`com.senter.flashlight`).
> Distilasi riset + verifikasi adversarial + eksekusi build, dari 4 persona:
> 🟢 Senku (first-principles) · 🔵 Sai (arsitektur kode) · 🟡 Chrome (DIY/MVP) · 🟣 Xeno (rilis & kepatuhan).
> Disusun: 13 Juni 2026. Semua klaim teknis telah diverifikasi ke dokumentasi resmi.

---

## 0. Ringkasan Keputusan (TL;DR)

| Aspek | Keputusan | Alasan inti (terverifikasi) |
|---|---|---|
| API senter | `CameraManager.setTorchMode()` | API 23+, **tanpa izin CAMERA**, tanpa buka sesi kamera |
| Izin runtime | **NOL `<uses-permission>`** | `setTorchMode` tidak ber-anotasi `@RequiresPermission` |
| UI | **Jetpack Compose** | Kecepatan review TIDAK bergantung ukuran; Compose memudahkan fitur nyata penangkal "Minimum Functionality" |
| Penyimpanan | **DataStore Preferences** | < 10 kunci key-value; SQLite/Room overkill |
| minSdk / target / compile | **23 / 36 / 36** | `setTorchMode` butuh 23; target aman lewati tenggat Agustus 2026 |
| Output | **AAB** ter-sign + Play App Signing | Wajib AAB; upload key bisa di-reset bila hilang |
| Data Safety | **"No data collected / No data shared"** | Tanpa SDK iklan/analitik/jaringan |

---

## 1. 🟢 Senku — Kenapa Nol Izin Itu Sah (First-Principles)

Privasi kamera = soal **membaca sensor gambar**. Menyalakan senter = soal **menulis satu bit ke driver arus LED**. Dua hal terpisah secara fisik → izin `CAMERA` tidak relevan untuk torch.

| Aspek | `Camera.open()` lama (deprecated) | **`setTorchMode()`** (API 23+) |
|---|---|---|
| Akses sensor gambar | Ya | **Tidak** |
| Izin `CAMERA` | **Wajib** | **Tidak wajib** |
| Buka sesi kamera | Ya (+ `SurfaceTexture`) | **Tidak** |
| Status | Usang | **Cara yang benar** |

**Esensi absolut senter:** `CameraManager` + 1 cameraId (`FLASH_INFO_AVAILABLE == true`) + `setTorchMode(id, on)` + 1 tombol. Sisanya lemak.

> ⚠️ **Jangan minta `android.permission.CAMERA`** — memicu prompt menakutkan tanpa alasan & merusak profil bersih.

---

## 2. 🔵 Sai — Saran Arsitektur Kode

- **State otoritatif dari hardware** lewat `CameraManager.TorchCallback`, bukan variabel lokal yang bisa basi (OS bisa mematikan torch saat overheat / app lain merebut kamera).
- **WAJIB overload `registerTorchCallback(callback, Handler)`** — overload `(Executor, callback)` baru ada di **API 28**, sedangkan minSdk 23. Salah overload = crash di API 23–27.
- **Penyimpanan — pilih DataStore, bukan SQLite/Room:**

  | | SharedPreferences | **DataStore** ✅ | Room/SQLite |
  |---|---|---|---|
  | Model | key-value | key-value | tabel relasional |
  | Thread | blocking main | **async (Flow)** | async |
  | Dampak AAB | ~0 | **kecil** | besar (runtime+compiler) |
  | Cocok senter? | legacy | **PALING COCOK** | hanya jika ada riwayat/log |

  > Pakai Room **hanya jika** kelak menambah riwayat pemakaian / statistik durasi nyala.

- **Deteksi hardware, jangan asumsi:** iterasi `cameraIdList` → cari `FLASH_INFO_AVAILABLE` → prioritas `LENS_FACING_BACK`. Device tanpa flash → tombol disabled, jangan crash.
- **Kecerahan berjenjang (opsional, API 33+):** `FLASH_INFO_STRENGTH_MAXIMUM_LEVEL > 1` = didukung → `turnOnTorchWithStrengthLevel`; `<= 1` = on/off saja.

---

## 3. 🟡 Chrome — Saran Praktis & Build

- **AAB, bukan APK universal** — Play kirim split per device, unduhan mini.
- **Aktifkan R8:** `isMinifyEnabled = true` + `isShrinkResources = true`. Hasil terbukti: **AAB rilis 1.9 MB**.
- **Nol library berat** — tanpa Firebase/analitik/iklan/Retrofit.
- **Keystore:** validity **≥ 25 tahun**, simpan **di luar repo/VCS**, backup password.
- **Checklist uji device ASLI** (emulator tak cukup untuk LED nyata):
  - [ ] Toggle nyala/mati
  - [ ] Rotasi layar → tetap nyala
  - [ ] Background → foreground → state sinkron
  - [ ] App kamera lain merebut (`CAMERA_IN_USE`) → tak crash
  - [ ] Device tanpa flash → tombol disabled
  - [ ] Nyala 2–3 menit → tak overheat
  - [ ] Swipe dari recent → senter benar-benar padam
  - [ ] SOS & senter layar berfungsi

---

## 4. 🟣 Xeno — Strategi Lolos Review Google Play

### Peta timeline per jenis akun

| Jenis Akun | Gate 12-tester/14-hari | SLA review app baru | Realistis "~1 hari"? |
|---|---|---|---|
| Personal baru (pasca 13 Nov 2023) | ❌ **Wajib** | + 14 hari testing dulu | ❌ Lantai ~15–21 hari |
| **Organisasi terverifikasi** ✅ | ✅ **Dikecualikan** | **~24–72 jam** best case | ✅ **Layak** |
| Update app yang sudah lolos | — | Jam-an–1 hari | ✅ Sangat cepat |

> Proyek ini memakai **akun Organisasi terverifikasi** → berada di jalur cepat.

### Checklist kepatuhan (profil review terbersih)

1. ✅ **Nol izin sistem** → form Data Safety paling sederhana.
2. ✅ **Privacy Policy URL publik WAJIB** meski nol data (kebijakan sejak April 2022). Tanpa URL aktif, form Data Safety tak bisa di-submit. (`PRIVACY_POLICY.md` sudah disiapkan — tinggal host.)
3. ✅ **Data Safety** = "No data collected / No data shared".
4. ✅ **Content rating** (IARC) jujur → Everyone.
5. ⚠️ **Pasar senter = lahan spam terpadat** → Google menyaring ekstra ketat: *Minimum Functionality*, *Spam*, *Misleading Metadata*. Mitigasi: fitur nyata stabil, **ikon orisinal**, **screenshot = layar app ASLI** (bukan mockup), nama tidak keyword-stuffing.

### Urutan rilis tercepat (jalur Organisasi)
1. Build AAB ter-sign → 2. Host Privacy Policy → 3. Store listing → 4. Data Safety "no data" → 5. Content rating → 6. Upload AAB ke Production → 7. Tunggu ~24–72 jam.

---

## 5. ✅ Fakta Terverifikasi (lolos uji adversarial)

| Klaim | Vonis |
|---|---|
| `setTorchMode()` ada sejak API 23 & tanpa izin CAMERA | ✅ Confirmed |
| `setTorchMode()` tidak membuka sesi kamera | ✅ Confirmed |
| `turnOnTorchWithStrengthLevel` & `getTorchStrengthLevel` = API 33 | ✅ Confirmed |
| Kontrol kecerahan didukung jika `STRENGTH_MAXIMUM_LEVEL > 1` | ✅ Confirmed |
| `FLASH_INFO_AVAILABLE` boolean per-cameraId (ada sejak API 21) | ✅ Confirmed |
| `registerTorchCallback` sinkronkan state torch | ✅ Confirmed |
| `screenBrightness` per-window TIDAK butuh `WRITE_SETTINGS` | ✅ Confirmed |
| QS Tile butuh `BIND_QUICK_SETTINGS_TILE` (signature, ditegakkan sistem — bukan izin user) | ✅ Confirmed |
| Akun personal pasca-Nov-2023 wajib closed testing 12 tester × 14 hari | ✅ Confirmed |

---

## 6. ⚠️ Pantangan & Koreksi Kritis (JANGAN diabaikan)

- **GATE TESTING akun personal = pembunuh target "1 hari".** Hanya akun Organisasi terverifikasi / akun lama production yang dikecualikan.
- **JANGAN tambah SDK iklan/analitik/Firebase.** Begitu masuk, `AD_ID` ter-merge ke manifest, Data Safety berubah "data collected", dan profil "no data" hancur. **Ini quick-win terbesar yang paling gampang dirusak.**
- **`registerTorchCallback` WAJIB overload `(callback, Handler)`** pada minSdk 23 (overload Executor baru API 28).
- **targetSdk — status tenggat *uncertain*:** minimum sah saat submit per Juni 2026 = **API 35**; set `targetSdk = 36` untuk aman, tapi **verifikasi ulang angka & tanggal di Play Console saat submit** (tenggat API 36 ~Agustus 2026 belum tercantum eksplisit di halaman resmi per Juni 2026).
- **Koreksi yang di-*refuted* (jangan diulang):** ambang *discoverability* app eksisting = **API 34** (app yang menarget API 33 atau lebih rendah jadi tak terlihat) — **bukan** API 35. Jangan campur ambang new-submission (35→36) dengan ambang discoverability (34).
- **Privacy Policy URL publik wajib** sebelum upload, meski nol data.
- **Keystore validity ≥ 25 thn**, simpan di luar repo, **backup** — hilang = tak bisa update app (kecuali reset upload key via Play App Signing).
- **Catatan jujur soal izin di APK:** ada satu entri `com.senter.flashlight.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` — itu **signature permission internal milik app sendiri** dari `androidx.core`, **bukan** izin sistem/user, **tak muncul di Play, tak menyentuh Data Safety**. Profil nol-izin tetap utuh.

---

## 7. 📌 Status Eksekusi (terbukti, bukan klaim)

| Verifikasi | Hasil |
|---|---|
| `:app:assembleDebug` | ✅ BUILD SUCCESSFUL |
| `:app:bundleRelease` (R8 + shrink) | ✅ AAB **1.9 MB** |
| `lintVitalRelease` | ✅ Lolos (nol error pemblokir) |
| Izin sistem di APK | ✅ NOL (hanya signature internal androidx) |
| Signing | ✅ Keystore terpasang, AAB ter-sign |

**Sisa langkah user:** host Privacy Policy ke URL publik → uji di HP asli → lengkapi Play Console → upload AAB ke Production.

---

*Kukuku... Sains tidak pernah berbohong. Saran ini sudah lolos uji — tinggal eksekusi rilis. — Kingdom of Science* 🧪
