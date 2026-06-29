# responsi-sda-route-optimization

## i. Route Optimization
Route Optimization adalah aplikasi web yang dirancang untuk mengoptimalkan proses pengiriman makanan melalui pencarian rute tercepat dan pengelolaan prioritas pengiriman. Sistem memanfaatkan representasi peta dalam bentuk Graph, algoritma Dijkstra (Shortest Path) untuk menentukan jalur paling efisien, serta Priority Queue untuk mengurutkan pesanan berdasarkan estimasi waktu pengiriman. Aplikasi ini memungkinkan admin mengelola data lokasi dan jalan, memvisualisasikan peta, menambahkan pesanan, serta memperoleh rekomendasi rute terbaik sehingga proses distribusi menjadi lebih cepat dan terorganisir.

## ii. Nama Anggota Kelompok
- Arjuna Bagus Wicaksono (L0125005)
- Dwiandra Alfa Syarif Ramadhan (L0125009)
- Linda Sihmawati (L0125049)

## iii. Fitur Utama Program
### 1. Representasi peta pakai Graph
Jalan dan gang di lingkungan perumahan direpresentasikan sebagai graph berbobot tak berarah:
- Simpul (node) = lokasi (restoran, gang, blok rumah pelanggan)
- Sisi (edge) = jalur yang menghubungkan dua lokasi, dengan bobot berupa waktu tempuh dalam menit
- Setiap jalur bersifat dua arah (kurir bisa lewat dari A ke B maupun B ke A)
- Peta default berisi 19 lokasi dan 30 jalur unik, mencakup 6 restoran, 5 gang utama, dan beberapa blok rumah pelanggan
### 2. Manajemen Prioritas Antrian Pengiriman
Setiap pesanan yang masuk otomatis dihitung estimasi waktu tempuhnya (lewat algoritma Dijkstra), lalu diurutkan dalam antrian prioritas:
- Pesanan dengan estimasi waktu tempuh tercepat ditempatkan di posisi prioritas tertinggi
- Pesanan yang tujuannya tidak terhubung ke peta akan ditandai sebagai "tidak terjangkau" dan tidak diberi estimasi
### 3. Pencarian Rute Terpendek (Shortest Path)
Menggunakan algoritma Dijkstra untuk mencari rute dengan total waktu tempuh minimum antara dua titik mana pun di peta

## iv. Struktur Data dan Algoritma
| Struktur/Algoritma | Digunakan di | Alasan Pemilihan |
|---|---|---|
| **Graph (Adjacency List)** | `Graph` | Representasi adjacency list dipilih dibanding adjacency matrix karena peta jalan bersifat *sparse* (setiap lokasi biasanya hanya terhubung ke beberapa lokasi tetangga, bukan ke semua lokasi). Adjacency list lebih hemat memori, kompleksitas ruang O(V + E) dibanding O(V²) pada matrix, dan operasi seperti menambah lokasi/jalur serta mengambil daftar tetangga bisa dilakukan dalam O(1). |
| **HashMap** | `Graph.locations`, `Graph.adjacencyList` | Dipakai untuk memetakan nama lokasi ke objek `Location` dan ke daftar tetangganya. Pencarian, penyisipan, dan pengecekan keberadaan lokasi semuanya O(1) rata-rata, jauh lebih cepat dibanding pencarian linear pada list biasa. |
| **Priority Queue (Min-Heap)** | `Dijkstra.shortestPath` | Dijkstra membutuhkan struktur yang selalu bisa mengambil simpul dengan jarak sementara terkecil secara efisien. Priority queue berbasis min-heap memberikan kompleksitas O(log V) untuk operasi ambil-minimum dan sisip, sehingga total algoritma menjadi O((V + E) log V), jauh lebih efisien dibanding pencarian linear simpul terdekat yang bisa mencapai O(V²). |
| **Algoritma Dijkstra** | `Dijkstra` | Dipilih karena bobot graph (waktu tempuh) selalu bernilai positif, dan Dijkstra menjamin solusi shortest path yang optimal untuk graph dengan bobot non-negatif. Algoritma ini juga mendukung pencarian rute antar titik mana pun secara umum, tidak terbatas pada satu pasangan asal-tujuan saja. |
| **Comparable / Custom Comparator** | `FoodOrder.compareTo` | Setiap pesanan perlu dibandingkan satu sama lain untuk menentukan urutan prioritas di antrian. Dengan mengimplementasikan `Comparable` (di versi Java) atau fungsi pembanding custom (di versi JavaScript), pesanan dapat diurutkan secara otomatis berdasarkan estimasi waktu tempuh tanpa perlu logika sorting manual yang berulang. |
| **Array terurut sebagai antrian prioritas** *(versi web)* | `OrderQueue` | Versi web menyusun ulang (`sort`) array pesanan setiap kali ada penambahan, sehingga selalu terurut dari prioritas tertinggi ke terendah. Pendekatan ini dipilih agar seluruh isi antrian mudah ditampilkan langsung sebagai tabel di antarmuka web, dibanding struktur heap murni yang hanya efisien untuk mengambil satu elemen prioritas teratas. |
 
---

## v. Panduan Instalasi dan Menjalankan Program
###  Prasyarat Sistem
* **Java Development Kit (JDK)**: Versi 8 atau yang lebih baru (Disarankan JDK 11 atau 17).
* **Visual Studio Code (VS Code)**.
* **Extension Pack for Java**: Ekstensi resmi Microsoft untuk mengeksekusi kode Java di VS Code.
* **GSON Library (JAR)**: Unduh file `gson-2.10.1.jar` (atau versi sejenis) dari MVNRepository.

### 1. Struktur Direktori Proyek
Sebelum memulai, pastikan tata letak komponen berkas kode sumber (*source code*) dan pustaka pihak ketiga di dalam folder kerja Anda disusun persis seperti skema gabungan di bawah ini:

```text
📁 nama-folder-proyek-anda/
├── 📁 .vscode/                         # Folder konfigurasi otomatis VS Code
│   └── settings.json                   # Fail backup library GSON (Solusi alternatif)
├── 📁 src/                             # Direktori khusus seluruh berkas Java
│   ├── DefaultMapData.java             # Berkas data peta internal
│   ├── FoodOrder.java                  # Model data & logika komparasi antrean
│   ├── MapGraph.java                   # Struktur data Graf & algoritma Dijkstra
│   └── RouteOptimizationApp.java       # Berkas utama (Main backend server)
├── index.html                          # Tampilan utama Dashboard (Frontend web)
```
### 2. Langkah Setup & Menjalankan Backend (Java via VS Code)

1. **Buka Folder Kerja di VS Code** Buka aplikasi VS Code, klik **File** > **Open Folder...**, lalu pilih folder direktori utama proyek Anda (bukan folder `src`-nya saja, tetapi folder induknya yang berisi `index.html` dan `gson-2.10.1.jar`).

2. **Memasukkan Library GSON ke Proyek Java** * Pada bilah navigasi kiri (*sidebar*) VS Code, gulir ke bawah hingga Anda menemukan panel bernama **Java Projects**.
   * Cari dan ekspand sub-menu **Referenced Libraries** di dalam panel tersebut.
   * Klik ikon tombol **`+` (Add Library)** yang terletak di sebelah kanan menu Referenced Libraries.
   * Arahkan ke berkas `gson-2.10.1.jar` yang berada di direktori utama folder proyek Anda, kemudian klik **OK/Open**. Langkah ini mutlak diperlukan agar pustaka pembaca JSON dapat dimuat tanpa memicu *error compilation*.

3. **Mengeksekusi Server (Run)** * Buka folder `src` lalu klik berkas utama server: **`RouteOptimizationApp.java`**.
   * Cari teks melayang (*CodeLens*) kecil bertuliskan **Run** tepat di atas baris fungsi `public static void main(String[] args)`, atau klik tombol ikon **Play/Run Java** yang berada di pojok kanan atas layar editor.

4. **Verifikasi Output Server** Jika server berhasil melakukan alokasi *socket*, panel **Terminal** terintegrasi di bagian bawah VS Code akan mengeluarkan log keluaran berikut:
   ```text
   =================================================
   Server RouteOptimization berjalan di http://localhost:8080
   =================================================
   ```
### 3. Langkah Menjalankan Frontend (Aplikasi Web)

Aplikasi klien (*frontend UI*) dirancang secara portabel sebagai *Single Page Application* murni menggunakan kombinasi Vanilla JS dan Tailwind CSS. Anda tidak memerlukan proses instalasi ataupun server web eksternal terpisah.

1. Buka folder kerja proyek Anda melalui jendela File Explorer bawaan sistem operasi komputer Anda.
2. Cari berkas bernama **`index.html`** yang berada di luar folder `src`.
3. **Klik ganda (double-click)** pada file `index.html` tersebut untuk membuka panel kendali kurir secara langsung melalui peramban internet (*web browser*) utama Anda, seperti Google Chrome, Mozilla Firefox, atau Microsoft Edge.

### 4. Prosedur Pengujian Alur Integrasi Aplikasi

Setelah server *backend* aktif dan halaman *frontend* berhasil dibuka, Anda dapat mencoba dan menguji fungsionalitas fitur sistem manajemen distribusi kurir melalui panel kontrol utama dengan alur sebagai berikut:

1. **Membuat Simulasi Input Pesanan Baru**
   * Perhatikan panel form **Input Pesanan Baru**. Isilah kolom **ID Pesanan** yang ingin disimulasikan (atau biarkan nomor urutan otomatis berjalan).
   * Pilih lokasi **Asal (Restoran)** dan lokasi **Tujuan (Blok Rumah Pelanggan)** melalui menu *dropdown*. 
   * Tekan tombol **Tambah ke Antrean**. Jika berhasil, sistem akan memicu *toast notification* sukses di pojok kanan bawah yang menandakan data berhasil masuk ke memori sistem.

2. **Analisis Rute dan Urutan Prioritas Antrean (*Priority Queue*)**
   * Ketika pesanan ditambahkan, data dikirim langsung ke server untuk diproses menggunakan **Algoritma Dijkstra**.
   * Di dalam tabel **Daftar Antrean Prioritas Pengiriman**, sistem secara otomatis mengurutkan baris pesanan berdasarkan kriteria bobot **Jarak Terpendek (km)** dan **Estimasi Waktu Tercepat (menit)**, bukan berdasarkan waktu input data.
   * Komponen antrean menampilkan informasi lengkap dari rute perjalanan: dari simpul asal mana, melewati jalur/gang apa saja, hingga tiba di simpul tujuan akhir, lengkap dengan analisis kalkulasi angka numeriknya.

3. **Visualisasi Jaringan Map Graph**
   * Di sisi kanan dashboard, Anda dapat melihat panel **Informasi Konektivitas Jaringan Peta (Map Graph)**.
   * Fitur ini menyajikan data pemetaan graf secara terperinci mengenai simpul-simpul asal yang terdaftar, hubungan koneksi ke simpul tetangganya (interkoneksi antar gang/blok), serta informasi bobot waktu *default* dalam satuan menit yang menjadi acuan perhitungan algoritma.

4. **Eksekusi dan Reset Antrean**
   * Anda dapat menekan ikon aksi (tombol hapus/selesai) pada setiap baris pesanan untuk menyimulasikan bahwa kurir telah menyelesaikan tugas pengantaran tersebut. Sistem akan langsung menghapusnya dan melakukan kalkulasi ulang pada sisa antrean.
   * Gunakan tombol **Reset Orders** jika ingin membersihkan seluruh daftar antrean secara instan dari layar monitor.
---
## vi. Library Eksternal
Aplikasi ini menggunakan **1 library eksternal (pihak ketiga)** sebagai komponen pendukung interkoneksi data:

* **Nama Library**: Google GSON (`gson-2.10.1.jar`)
* **Tempat Penggunaan (Dimana Digunakan)**: 
  * Berkas Utama: **`src/RouteOptimizationApp.java`**
  * Tanda Impor Kode Sumber (paling atas):
    ```java
    import com.google.gson.Gson;
    ```
* **Fungsi Utama**: 
  Library ini bertugas menjembatani komunikasi data antara *backend* dan *frontend*. GSON melakukan proses *serialization* dan *deserialization*, yaitu mengubah struktur data objek internal Java (seperti hasil kalkulasi rute terpendek dari Algoritma Dijkstra) menjadi format teks **JSON**. Format JSON inilah yang kemudian dikirimkan melalui HTTP Server agar bisa dibaca, diurai, dan ditampilkan secara grafis oleh komponen *frontend* (`index.html`).

---

