# Kebijakan Privasi — Senter

_Terakhir diperbarui: 13 Juni 2026_

Aplikasi **Senter** (paket `com.smile.senter`) dirancang dengan prinsip privasi maksimum.

## Data yang kami kumpulkan
**Tidak ada.** Aplikasi ini:

- **Tidak mengumpulkan** data pribadi apa pun.
- **Tidak mengirim** data ke server mana pun (tidak ada koneksi jaringan).
- **Tidak membagikan** data ke pihak ketiga.
- **Tidak meminta izin (permission)** sistem apa pun.
- **Tidak menampilkan iklan** dan **tidak memuat SDK analitik**.

## Penyimpanan lokal
Aplikasi hanya menyimpan **preferensi tampilan** (mode gelap/terang dan opsi "matikan
otomatis saat keluar") di **penyimpanan lokal perangkat** menggunakan Android DataStore.
Data ini tidak pernah meninggalkan perangkat dan terhapus saat aplikasi di-uninstall.

## Akses perangkat keras
Aplikasi mengontrol lampu flash (LED) melalui `CameraManager.setTorchMode()`, yang
**tidak membuka kamera** dan **tidak mengakses sensor gambar** — sehingga tidak ada
foto/video yang pernah diambil atau dibaca.

## Kontak
Pertanyaan terkait privasi: **smilecucok05@gmail.com**
