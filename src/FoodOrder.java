public class FoodOrder implements Comparable<FoodOrder> {

    // CATATAN: bobot edge pada MapGraph adalah WAKTU TEMPUH (menit), bukan jarak
    // Karena itu jarak (km), diturunkan dari waktu tempuh terpendek dikali kecepatan rata-rata kurir.
    private static final double KECEPATAN_KURIR_KM_PER_MENIT = 0.5; // setara 30 km/jam

    private String id;
    private String asal;
    private String tujuan;
    private int jarakKm;       // jarak terpendek hasil Dijkstra (km)
    private int estimasiMenit; // estimasi waktu pengiriman (menit), dihitung otomatis

    /**
     * Membuat pesanan baru dengan estimasi yang dihitung otomatis dari peta (Graph + Dijkstra).
     * @param id    ID pesanan
     * @param asal  lokasi asal (restoran) — harus ada di MapGraph
     * @param tujuan lokasi tujuan (pelanggan) — harus ada di MapGraph
     * @param peta  objek MapGraph yang berisi peta jalan
     */
    public FoodOrder(String id, String asal, String tujuan, MapGraph peta) {
        this.id = id;
        this.asal = asal;
        this.tujuan = tujuan;

        Dijkstra.RouteResult hasil = peta.findShortestPath(asal, tujuan);

        if (hasil.isReachable()) {
            this.estimasiMenit = (int) Math.round(hasil.totalDistance);
            this.jarakKm = (int) Math.round(hasil.totalDistance * KECEPATAN_KURIR_KM_PER_MENIT);
        } else {
            // Tidak ada rute yang menghubungkan asal -> tujuan di peta.
            this.estimasiMenit = Integer.MAX_VALUE;
            this.jarakKm = Integer.MAX_VALUE;
        }
    }

    // GETTER
    public String getId() {
        return id;
    }

    public String getAsal() {
        return asal;
    }

    public String getTujuan() {
        return tujuan;
    }

    public int getJarakKm() {
        return jarakKm;
    }

    public int getEstimasiMenit() {
        return estimasiMenit;
    }

    /**
     * @return true jika rute dari asal ke tujuan ditemukan di peta.
     */
    public boolean isReachable() {
        return estimasiMenit != Integer.MAX_VALUE;
    }

    // COMPARABLE
    /**
     * Membandingkan pesanan berdasarkan estimasi waktu.
     * Pesanan dengan estimasi lebih kecil dianggap lebih prioritas (urgent).
     * Digunakan oleh PriorityQueue untuk mengurutkan secara otomatis.
     */
    @Override
    public int compareTo(FoodOrder other) {
        return Integer.compare(this.estimasiMenit, other.estimasiMenit);
    }

    // DISPLAY
    @Override
    public String toString() {
        if (!isReachable()) {
            return String.format("| %-8s | %-15s | %-15s | %-10s | %-14s |",
                    id, asal, tujuan, "N/A", "N/A");
        }
        return String.format("| %-8s | %-15s | %-15s | %-10d | %-14d |",
                id, asal, tujuan, jarakKm, estimasiMenit);
    }

    /**
     * Menampilkan detail pesanan.
     */
    public void tampilDetail() {
        System.out.println("  ID         : " + id);
        System.out.println("  Asal       : " + asal);
        System.out.println("  Tujuan     : " + tujuan);
        if (!isReachable()) {
            System.out.println("  Status     : Rute tidak ditemukan (lokasi tidak terhubung)");
            return;
        }
        System.out.println("  Jarak      : " + jarakKm + " km");
        System.out.println("  Estimasi   : " + estimasiMenit + " menit");
    }
}
