# ✅ Jawaban Form "App Content" Play Console — Senter (`com.senter.flashlight`)

Lembar klik-demi-klik untuk semua deklarasi wajib. Disusun dari fakta terverifikasi:
**manifest nol `<uses-permission>`, nol SDK iklan/analitik, 100% offline, nol AD_ID.**

> ⚠️ Prinsip kunci: **preferensi lokal** (mode gelap, auto-off) disimpan di perangkat via
> DataStore dan **tidak pernah keluar dari perangkat** → menurut definisi Google itu **BUKAN
> "data collected"**. Jadi semua deklarasi pengumpulan/berbagi data = **TIDAK**.

---

## 1. 🔒 Data safety

**Bagian 1 — Data collection and security**
| Pertanyaan | Jawaban |
|---|---|
| Does your app collect or share any of the required user data types? | **No** |
| Is all of the user data collected by your app encrypted in transit? | *(otomatis N/A — tak ada data)* |
| Do you provide a way for users to request that their data is deleted? | *(otomatis N/A — tak ada data)* |

**Bagian 2 — Data types**
→ Karena jawaban di atas **No**, lewati semua kategori. Pastikan **TIDAK** ada yang tercentang:
Location, Personal info, Financial info, Health & fitness, Messages, Photos/videos,
Audio files, Files & docs, Calendar, Contacts, App activity, Web browsing,
**App info & performance** (crash log/diagnostics — kita tak punya), Device or other IDs.

**Hasil ringkasan Data Safety yang akan tampil di Play:**
- ✅ **No data shared with third parties**
- ✅ **No data collected**

---

## 2. 📢 Ads
| Pertanyaan | Jawaban |
|---|---|
| Does your app contain ads? | **No** |

---

## 3. 🆔 Advertising ID
| Pertanyaan | Jawaban |
|---|---|
| Does your app use advertising ID? | **No** |

> Benar: app tak menyertakan SDK iklan/GMS ads, jadi tak ada permission `AD_ID`.
> (Jika Play menampilkan peringatan AD_ID, abaikan — memang tidak dipakai.)

---

## 4. 🔑 App access
| Pertanyaan | Jawaban |
|---|---|
| Is all functionality available without special access? | **Ya — All functionality is available without special access** |

> App tak punya login, akun, kode, atau lokasi terkunci. Semua fitur langsung bisa dipakai.

---

## 5. 🎯 Content rating (kuesioner IARC)

| Langkah | Jawaban |
|---|---|
| Email | (email developer) |
| Kategori aplikasi | **Utility, Productivity, Communication, or Other** (Tools) |
| Kekerasan | **No** |
| Konten seksual | **No** |
| Bahasa kasar/profanity | **No** |
| Zat terkontrol (narkoba/alkohol/tembakau) | **No** |
| Perjudian (gambling) | **No** |
| Konten menakutkan/horror | **No** |
| User-generated content / interaksi sosial | **No** |
| Berbagi lokasi pengguna ke pengguna lain | **No** |
| Pembelian digital | **No** |

**Hasil yang diharapkan:** **Everyone / PEGI 3 / Rated for 3+** (semua umur).

---

## 6. 👥 Target audience and content
| Pertanyaan | Rekomendasi |
|---|---|
| Target age groups | **Pilih 13+ / 16+ / 18+ (JANGAN centang kelompok di bawah 13)** |
| Appeal to children? | **No** |

> 💡 **Kenapa hindari <13:** mencentang kelompok anak memasukkan app ke **Families Policy**
> (syarat tambahan & review lebih ketat). Senter utilitas netral — cukup target remaja/dewasa,
> tetap bisa diunduh siapa saja. Bila kalian SENGAJA mau program "Designed for Families",
> baru pilih kelompok anak + lengkapi syarat tambahannya.

---

## 7. 📋 Deklarasi lain (semua TIDAK)
| Deklarasi | Jawaban |
|---|---|
| News app? | **No** |
| COVID-19 contact tracing/status app? | **No** |
| Government app? | **No** |
| Financial features? | **No** |
| Health apps (health content)? | **No** |
| Data safety: data dipakai untuk tujuan lain? | **No** |

---

## 8. 🔗 Privacy policy
| Field | Isi |
|---|---|
| Privacy policy URL | **(WAJIB diisi)** — host `PRIVACY_POLICY.md` ke URL publik dulu (GitHub Pages / Notion / situs), lalu tempel URL-nya |

> Email kontak di kebijakan sudah di-set: `smilecucok05@gmail.com`.
> Tanpa URL publik aktif, form tak bisa di-submit (kebijakan sejak April 2022).

---

## ✅ Setelah semua hijau
1. Upload **AAB**: `app/build/outputs/bundle/release/app-release.aab` (bukan APK).
2. Lengkapi Store listing (judul/deskripsi → `STORE_LISTING.md`) + aset grafis (`assets/`, `screenshots/`).
3. Kirim ke review. Jalur akun **Organisasi terverifikasi** ≈ 24–72 jam.
