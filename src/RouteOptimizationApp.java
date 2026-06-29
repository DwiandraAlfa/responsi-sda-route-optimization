import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RouteOptimizationApp {

    // Struktur data untuk menampung Graf (Peta Lokasi)
    private static final Map<String, List<Node>> graph = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // Inisialisasi data peta/graf (Bisa disesuaikan dengan kebutuhan praktikum)
        inisialisasiPeta();

        // Membuat server berjalan di port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Endpoint 1: Cek Pesanan / Tes Koneksi (http://localhost:8080/api/pesanan)
        server.createContext("/api/pesanan", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                tambahkanCorsHeader(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                String response = "[]"; // Mengembalikan array kosong untuk indikator server aktif
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        // Endpoint 2: Kalkulasi Rute Dijkstra (http://localhost:8080/api/rute)
        server.createContext("/api/rute", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                tambahkanCorsHeader(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                // Ambil parameter query (?from=...&to=...)
                Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
                String from = params.get("from");
                String to = params.get("to");

                HasilDijkstra hasil = hitungDijkstra(from, to);

                // Konversi hasil ke format JSON menggunakan GSON
                Gson gson = new Gson();
                String responseJson = gson.toJson(hasil);

                byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("=================================================");
        System.out.println("Server RouteOptimization berjalan di http://localhost:8080");
        System.out.println("=================================================");
    }

    // Fungsi Pengaman CORS agar terintegrasi otomatis dengan Frontend
    private static void tambahkanCorsHeader(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }

    // Inisialisasi titik koordinat/peta simulasi logistik
    private static void inisialisasiPeta() {
        // Contoh penambahan jalur antar Resto ke Blok Perumahan (Sesuaikan graf kelompokmu di sini)
        tambahJalur("Resto Kenanga Asri", "Blok Cendana 1", 4);
        tambahJalur("Resto Kenanga Asri", "Blok Cendana 2", 8);
        tambahJalur("Blok Cendana 1", "Blok Cendana 3", 3);
        tambahJalur("Blok Cendana 2", "Blok Cendana 3", 2);
        tambahJalur("Blok Cendana 3", "Blok Akasia 1", 5);
        tambahJalur("Resto Pondok Mertua", "Blok Akasia 1", 6);
        tambahJalur("Blok Akasia 1", "Blok Akasia 2", 4);
        
        // Pelengkap otomatis untuk opsi select option di HTML
        tambahJalur("Resto Graha Mawar", "Blok Cendana 4", 5);
        tambahJalur("Resto Ayam Geprek", "Blok Akasia 3", 4);
        tambahJalur("Resto Bebek Slamet", "Blok Akasia 4", 6);
        tambahJalur("Resto Warmindo Selera", "Blok Cendana 2", 3);
        tambahJalur("Blok Cendana 4", "Blok Akasia 2", 4);
        tambahJalur("Blok Akasia 3", "Blok Akasia 4", 3);
    }

    private static void tambahJalur(String asal, String tujuan, int bobotJarak) {
        graph.computeIfAbsent(asal, k -> new ArrayList<>()).add(new Node(tujuan, bobotJarak));
        graph.computeIfAbsent(tujuan, k -> new ArrayList<>()).add(new Node(asal, bobotJarak)); // Graf tidak berarah
    }

    // Implementasi Inti Core Algoritma Dijkstra
    private static HasilDijkstra hitungDijkstra(String asal, String tujuan) {
        if (!graph.containsKey(asal) || !graph.containsKey(tujuan)) {
            return new HasilDijkstra(new ArrayList<>(), 0);
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.bobot));

        for (String vertex : graph.keySet()) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(asal, 0);
        pq.add(new Node(asal, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String u = current.namaTarget;

            if (u.equals(tujuan)) break;

            if (current.bobot > distances.get(u)) continue;

            for (Node tetangga : graph.getOrDefault(u, new ArrayList<>())) {
                int alt = distances.get(u) + tetangga.bobot;
                if (alt < distances.get(tetangga.namaTarget)) {
                    distances.put(tetangga.namaTarget, alt);
                    predecessors.put(tetangga.namaTarget, u);
                    pq.add(new Node(tetangga.namaTarget, alt));
                }
            }
        }

        // Rekonstruksi rute terpendek yang dilewati
        List<String> path = new ArrayList<>();
        String step = tujuan;
        if (predecessors.containsKey(step) || step.equals(asal)) {
            while (step != null) {
                path.add(0, step);
                step = predecessors.get(step);
            }
        }

        int totalDistance = distances.get(tujuan) == Integer.MAX_VALUE ? 0 : distances.get(tujuan);
        return new HasilDijkstra(path, totalDistance);
    }

    // Helper mengubah query URL menjadi Map data
    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    // Kelas Representasi Simpul Objek (Node)
    static class Node {
        String namaTarget;
        int bobot;

        Node(String namaTarget, int bobot) {
            this.namaTarget = namaTarget;
            this.bobot = bobot;
        }
    }

    // Kelas Model Representasi data balikan (Response Object) ke Frontend
    static class HasilDijkstra {
        List<String> path;
        int totalDistance;

        HasilDijkstra(List<String> path, int totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }
    }
}