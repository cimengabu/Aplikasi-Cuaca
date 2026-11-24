# Skyora ☀️🌧️🍃

Aplikasi cuaca Android modern, estetis, dan lengkap yang dibangun menggunakan **Jetpack Compose**, **Material 3**, arsitektur **MVVM**, serta data real-time dari **Open-Meteo API** (tanpa API key).

---

## ✨ Fitur Lengkap

### 🏠 Fitur Utama
- 🧭 **Navigasi Bawah (Bottom Navigation)**: 3 halaman utama (**Beranda**, **Favorit**, **Pengaturan**) dengan ikon dan label resmi Material 3.
- ⭐ **Kota Favorit**: Simpan kota aktif ke daftar favorit secara instan menggunakan ikon bintang di Beranda; kelola dan hapus daftar kota favorit dengan dialog konfirmasi. Data tersimpan permanen di **DataStore**.
- ⚙️ **Pengaturan Lengkap**:
  - Satuan suhu: **Celsius (°C)** / **Fahrenheit (°F)**
  - Satuan kecepatan angin: **km/h** / **mph**
  - Tema tampilan: **Ikuti Sistem**, **Terang**, atau **Gelap** (langsung berlaku secara dinamis)
  - Pilihan Bahasa: **Bahasa Indonesia**, **English**, dan **Basa Jawa**
- 📊 **Detail Cuaca Tambahan**: Tekanan udara (hPa), Tutupan awan (%), Arah angin (dengan teks mata angin), Jarak pandang (km), Indeks UV maksimum (dengan lencana kategori & warna), serta waktu Matahari Terbit & Terbenam.
- 🔄 **Pull to Refresh**: Tarik ke bawah di Beranda untuk memuat ulang data dengan animasi indikator modern Material 3, dilengkapi label status *"Terakhir diperbarui"*.
- 🎨 **Latar Belakang Dinamis**: Background animasi cuaca interaktif dengan partikel hujan/awan/matahari yang hidup mengikuti kondisi cuaca aktual.

### 🚀 Fitur Unggulan Dashboard & Tampilan ala Xiaomi / HyperOS
- 📱 **Desain Dashboard Modern Translucent (HyperOS Style)**:
  - **Hero Temperature Section**: Angka temperatur besar yang mengambang bebas di atas latar belakang cuaca bergerak tanpa terkurung kotak kaku, dilengkapi subtitle elegan satu baris (`Kondisi  Min° / Max°  Kualitas udara: Skor - Status`).
  - **Kartu Kualitas Udara (Circular Gauge)**: Menampilkan kategori AQI (Moderate, Baik, dsb.) di sebelah kiri dan instrumen busur lingkaran skor AQI di sebelah kanan persis seperti di aplikasi cuaca bawaan ponsel.
  - **Prakiraan Per Jam Terpadu**: Scroll horizontal 24 jam dalam satu kartu kaca transparan (*frosted glass*) dengan penanda *"Sekarang"*, ikon cuaca, dan suhu.
  - **Prakiraan Multi-Hari dengan Kurva Garis & Toggle Garis/Daftar**: Memilih mode grafik kurva suhu halus antar-hari atau mode daftar vertikal.
- 📈 **Grafik Tren Suhu 24 Jam (Canvas)**: Kurva temperatur halus yang digambar manual menggunakan Android Canvas (tanpa library eksternal) dengan titik data, label suhu, dan area gradasi transparan di bawah garis, dapat digeser secara horizontal.
- 🍃 **Kualitas Udara (AQI)**: Mengambil data indeks kualitas udara dari Open-Meteo Air Quality API, menampilkan angka AQI, kategori berskala warna standar US EPA (Baik, Sedang, dsb.), bar indikator level, dan konsentrasi polutan PM2.5 & PM10.
- 📋 **Detail Hari (Modal Bottom Sheet)**: Klik pada salah satu kartu prakiraan harian untuk membuka sheet interaktif berisi rincian lengkap hari tersebut (probabilitas hujan, total curah hujan mm, indeks UV max, sunrise/sunset).
- 💡 **Saran Hari Ini (Tips Cuaca Otomatis)**: Rekomendasi kontekstual pintar (misal: membawa payung saat peluang hujan tinggi, tabir surya saat UV tinggi, minum cukup air saat panas, masker saat AQI buruk).
- 📶 **Cache Offline**: Menyimpan respon cuaca terakhir ke DataStore. Saat offline, aplikasi secara otomatis menampilkan data cache disertai banner pemberitahuan status offline tanpa membuat aplikasi crash.
- ✨ **Animasi Halus**: Transisi crossfade antar halaman dan animasi kemunculan elemen kartu dengan fade dan slide.

### 📍 Fitur Ekstra & Ketepatan Data
- 🛰️ **Lokasi GPS Cerdas ("Gunakan Lokasi Saya")**: Menggunakan Google Play Services Location (`FusedLocationProviderClient.getCurrentLocation` + fallback `lastLocation`) dan Android `Geocoder` di thread background. Menangani semua skenario izin dan GPS mati secara komprehensif dengan Snackbar interaktif (tombol menuju Pengaturan Izin / Pengaturan Layanan Lokasi).
- 🎯 **Akurasi Label Cuaca Berbasis Curah Hujan & Tutupan Awan**: Memadukan WMO weather code dengan nilai curah hujan (`precipitation`) dan tutupan awan (`cloud_cover`). Menghilangkan anomali label "Gerimis" saat langit hanya berawan tanpa hujan nyata (< 0.2 mm). Nilai curah hujan (mm) dan tutupan awan (%) ditampilkan transparan pada kartu utama dan kartu rincian.
- ⏱️ **Prediksi Menit ke Depan (Nowcasting Card)**: Menggunakan Open-Meteo `minutely_15` API untuk mendeteksi kapan hujan mulai atau reda dalam rentang 15–60 menit ke depan, lengkap dengan timeline blok interval 15 menit.
- 👋 **Banner Sambutan Pertama Kali**: Pengguna baru disambut dengan dialog/banner ramah untuk memilih lokasi GPS atau mencari kota, tanpa langsung memaksakan kota default tanpa penjelasan.
- 🔍 **Pencarian Kota Bersih & Koordinat Transparan**: Menampilkan wilayah administratif (`admin1`), negara, serta menyaring duplikat hasil pencarian. Koordinat latitude dan longitude lokasi aktif ditampilkan rapi di bawah nama kota.
- 🌐 **Dukungan Tiga Bahasa**: Dilengkapi terjemahan lengkap dalam **Bahasa Indonesia** (`values-in`), **English** (`values`), dan **Basa Jawa** (`values-jv`).
- ℹ️ **Transparansi Sumber Data**: Label atribusi resmi *"Sumber: Open-Meteo (model prakiraan)"* pada layar Pengaturan.

---

## 🛠️ Tech Stack

| Komponen | Pilihan Teknologi |
|---|---|
| **Bahasa Pemrograman** | Kotlin |
| **UI Toolkit** | Jetpack Compose + Material 3 |
| **Penyimpanan Lokal** | Jetpack DataStore Preferences + Gson |
| **Arsitektur** | Clean MVVM (Data Layer, ViewModel, UI) |
| **Networking** | Retrofit 2 + Gson Converter + Coroutines |
| **Lokasi** | Google Play Services Location |
| **Grafik** | Custom Canvas Drawing (Cubic Bezier Curve) |
| **Multi Bahasa** | Android String Resources (`values`, `values-in`, `values-jv`) + `AppCompatDelegate.setApplicationLocales` |
| **Mode Tema** | Material 3 Color Schemes + `AppCompatDelegate.setDefaultNightMode` |

---

## 🏗️ Struktur Folder Proyek

```
com.example.cuacaku/
├── MainActivity.kt                  # Activity utama (AppCompatActivity)
├── data/
│   ├── WeatherRepository.kt         # Repository koordinasi API
│   ├── local/
│   │   ├── FavoritesRepository.kt   # Penyimpanan kota favorit (DataStore JSON)
│   │   ├── SettingsRepository.kt    # Penyimpanan preferensi satuan, tema, & bahasa
│   │   └── WeatherCache.kt          # Cache data cuaca offline
│   └── remote/
│       ├── Dto.kt                   # Data Transfer Objects API
│       └── WeatherApi.kt            # Retrofit interfaces & singleton NetworkModule
├── ui/
│   ├── WeatherViewModel.kt          # ViewModel cuaca beranda & cache
│   ├── FavoritesViewModel.kt        # ViewModel kota favorit
│   ├── SettingsViewModel.kt         # ViewModel pengaturan & satuan
│   ├── navigation/
│   │   └── AppNavigation.kt         # Bottom Navigation Bar & Crossfade
│   ├── home/
│   │   └── HomeScreen.kt            # Dashboard utama
│   ├── favorites/
│   │   └── FavoritesScreen.kt       # Layar kota favorit
│   ├── settings/
│   │   └── SettingsScreen.kt        # Layar pengaturan
│   ├── components/
│   │   ├── CurrentWeatherCard.kt    # Kartu utama dengan gradasi dinamis
│   │   ├── StatsRow.kt              # Kartu kelembapan, angin, dan terasa
│   │   ├── HourlyRow.kt             # Scroll 24 jam
│   │   ├── HourlyTemperatureChart.kt# Grafik kurva suhu Canvas
│   │   ├── DailyCard.kt             # Kartu harian 7 hari
│   │   ├── DayDetailBottomSheet.kt  # Bottom sheet rincian harian
│   │   ├── WeatherDetailsCard.kt    # Grid rincian cuaca tambahan
│   │   ├── AirQualityCard.kt        # Kartu AQI & PM2.5/PM10
│   │   ├── WeatherTipsCard.kt       # Kartu saran hari ini
│   │   ├── FavoriteCityCard.kt      # Kartu kota favorit
│   │   ├── CityResultItem.kt        # Hasil pencarian kota
│   │   └── LanguageDialog.kt        # Dialog pilih bahasa
│   └── theme/
│       └── Theme.kt                 # Material 3 Light & Dark Color Schemes
└── util/
    ├── WeatherCode.kt               # Mapping kode WMO ke emoji & deskripsi
    ├── WeatherTips.kt               # Logika penentuan saran cuaca
    ├── AqiHelper.kt                 # Klasifikasi US AQI
    ├── DirectionHelper.kt           # Konversi derajat ke mata angin
    └── UnitConverter.kt             # Konversi suhu & kecepatan angin
```

---

## 🔄 Alur Data

```
Open-Meteo API / GPS
         │
         ▼
WeatherRepository & NetworkModule
         │
         ├───► WeatherCache / Settings / Favorites (DataStore)
         ▼
ViewModels (WeatherViewModel, FavoritesViewModel, SettingsViewModel)
         │  (StateFlow & Lifecycle-aware collection)
         ▼
Jetpack Compose UI (Screens & Components)
```

---

## 🚀 Cara Menjalankan

1. **Clone repository ini**:
   ```bash
   git clone https://github.com/username/CuacaKu.git
   ```
2. Buka folder proyek di **Android Studio** (versi Ladybug / Meerkat atau yang lebih baru).
3. Pastikan JDK Gradle mengarah ke **Android Studio JBR** (Java 17/21/25).
4. Tunggu Gradle Sync selesai secara otomatis.
5. Jalankan aplikasi pada Emulator atau Perangkat Fisik (Minimal Android 7.0 / SDK 24).

---

## 📸 Dokumentasi & Screenshot

| Mode Terang (Beranda) | Mode Gelap (Beranda) |
|---|---|
| *(tambahkan screenshot)* | *(tambahkan screenshot)* |

| Grafik Suhu & AQI | Detail Hari (Bottom Sheet) |
|---|---|
| *(tambahkan screenshot)* | *(tambahkan screenshot)* |

| Kota Favorit | Pengaturan & Satuan |
|---|---|
| *(tambahkan screenshot)* | *(tambahkan screenshot)* |

---

## 👤 Pembuat Proyek

- **Nama**: *(Isi nama kamu)*
- **Kelas**: *(Isi kelas)*
- **No. Absen**: *(Isi nomor absen)*
- **Mata Pelajaran**: Pemrograman Mobile
