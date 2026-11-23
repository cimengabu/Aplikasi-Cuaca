# CuacaKu ☀️🌧️

Aplikasi cuaca Android modern yang menampilkan prakiraan cuaca menggunakan data dari **Open-Meteo API** (gratis, tanpa API key).

## ✨ Fitur

- 🌡️ **Cuaca saat ini** — suhu, kelembapan, kecepatan angin, dan kondisi cuaca
- ⏰ **Prakiraan per jam** — 24 jam ke depan dalam scroll horizontal
- 📅 **Prakiraan 7 hari** — suhu max/min dan probabilitas hujan
- 🔍 **Pencarian kota** — cari kota di seluruh dunia
- 🌐 **Multi bahasa** — Bahasa Indonesia dan English, bisa diganti dari dalam aplikasi
- 🌓 **Mode terang & gelap** — tampilan rapi di kedua mode
- 🔄 **Aman saat rotasi** — data dan input tidak hilang saat layar diputar

## 🛠️ Teknologi

| Komponen | Teknologi |
|---|---|
| Bahasa | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arsitektur | MVVM (ViewModel + StateFlow) |
| Networking | Retrofit + Gson + Coroutines |
| Multi bahasa | strings.xml (en/in) + AppCompat per-app language |
| Tema | AppCompat DayNight + Material 3 color scheme |
| Build | Gradle Kotlin DSL |

## 🏗️ Arsitektur & Alur Data

```
Open-Meteo API  →  Retrofit  →  Repository  →  ViewModel (StateFlow)  →  Compose UI
```

1. **API** — Data cuaca diambil dari Open-Meteo Forecast API dan Geocoding API
2. **Repository** — Membungkus panggilan API, menangani null safety
3. **ViewModel** — Menyimpan state UI (Loading/Error/Success), mengelola pencarian kota
4. **UI (Compose)** — Menampilkan data dalam card-card yang responsif

## 🚀 Cara Menjalankan

1. Clone repository ini:
   ```bash
   git clone https://github.com/username/CuacaKu.git
   ```
2. Buka project di **Android Studio** (Ladybug atau lebih baru)
3. Tunggu Gradle sync selesai
4. Klik **Run** ▶️ pada emulator atau perangkat fisik (minSdk 24)

## 📸 Screenshot

| Mode Terang | Mode Gelap |
|---|---|
| *(tambahkan screenshot)* | *(tambahkan screenshot)* |

| Landscape | Bahasa Inggris |
|---|---|
| *(tambahkan screenshot)* | *(tambahkan screenshot)* |

## 👤 Pembuat

- **Nama**: *(isi nama kamu)*
- **Kelas**: *(isi kelas)*
- **No. Absen**: *(isi nomor absen)*
