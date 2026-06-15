# 📦 Aset Play Store — Senter (`com.smile.senter`)

Disusun otomatis oleh Kingdom of Science (15 Jun 2026). Semua dimensi sudah lolos spec Play.

---

## ✅ Aset Siap Unggah

| Aset | File untuk diunggah | Dimensi | Format | Catatan |
|---|---|---|---|---|
| **Ikon hi-res** | `assets/icon_512.png` | 512×512 | PNG (alpha) | Sesuai spec (32-bit PNG with alpha) |
| **Feature graphic** | `assets/feature_1024x500.jpg` | 1024×500 | JPEG (no-alpha) | **Pakai .jpg** (versi .png ber-alpha, dilarang Play) |
| **Screenshot (id-ID)** | `captioned/*.png` (5) | 1080×1920 | PNG | UI Indonesia + caption ID — untuk listing **id-ID** |
| **Screenshot (en-US)** | `captioned_en/*.png` (5) | 1080×1920 | PNG | UI Inggris + caption EN — untuk listing **en-US** |

> App kini **dwibahasa** (default Indonesia + `values-en` English, ikut bahasa HP). Unggah set screenshot sesuai bahasa listing.
> Tanpa caption (polos): `screenshots/` (id) & `screenshots_en/` (en). Mentah 1080×2400: `raw/` & `raw_en/`.
> Sumber editable: `assets/icon.svg`, `assets/feature.svg` (render ulang via `qlmanage -t -s 1024 -o . feature.svg`).

---

## 🖼️ Urutan Screenshot Disarankan + Caption

| Urut | File | State | Caption usulan (overlay opsional) |
|---|---|---|---|
| 1 | `01_on_dark.png` | Senter NYALA (gelap) | "Satu ketuk, langsung terang" |
| 2 | `03_off_light.png` | Senter MATI (terang) | "Antarmuka bersih — tanpa iklan" |
| 3 | `04_sos_dark.png` | SOS aktif | "Sinyal darurat SOS Morse" |
| 4 | `06_off_dark.png` | Mode gelap | "Mode gelap hemat baterai" |
| 5 | `02_on_light.png` | NYALA (terang) | "Nol izin · 100% offline" |

> ⚠️ Screenshot "Senter Layar" (layar putih penuh) sengaja **dibuang** — sebagai gambar toko tampak kosong. Bila tetap diinginkan, perlu caption/overlay agar bermakna.
> 💡 Menambah caption + bingkai device di sekeliling screenshot **diizinkan** Play (selama isi UI asli, bukan mockup palsu).

---

## 🔧 Cara Regenerasi Screenshot (demo-mode)

Demo-mode HANYA aktif di build **debug** (`BuildConfig.DEBUG`), di-strip di release.

```bash
ADB=~/Library/Android/sdk/platform-tools/adb
S=emulator-5554
ACT=com.smile.senter/.MainActivity
# bersihkan dialog & status bar dulu:
$ADB -s $S shell settings put global hide_error_dialogs 1
# scene: on | off | sos | screenlight   ·   theme: light | dark
$ADB -s $S shell am start -n $ACT --ez demo true --es scene on --es theme dark
$ADB -s $S shell screencap -p /sdcard/s.png && $ADB -s $S pull /sdcard/s.png .
# crop 2400->1920: sips -c 1920 1080 s.png --out out.png
```

---

## 📋 Sisa Sebelum Publish (di luar aset)

- [x] ~~Host Privacy Policy ke URL publik~~ ✅ **LIVE: https://rizkiwk.github.io/senter-privacy/** (GitHub Pages, repo `rizkiwk/senter-privacy`).
- [ ] Store listing: judul, deskripsi singkat & panjang (hindari keyword-stuffing — pasar senter disaring ketat).
- [ ] Data Safety: "No data collected / No data shared".
- [ ] Content rating (IARC) → Everyone.
- [ ] Bump `versionCode` bila AAB versi 1 sudah pernah diunggah.
- [ ] Upload **AAB** (`app/build/outputs/bundle/release/app-release.aab`) — bukan APK.
