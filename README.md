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


## vi. Library Eksternal

