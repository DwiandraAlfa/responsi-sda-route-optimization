import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

//==================================
/* Class Location: merepresentasikan satu titik di peta, 
   misal restoran, rumah pelanggan, atau persimpangan jalan/gang. */
//==================================
class Location {
    private String name;
    
    public Location(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof Location)) return false;
        Location other = (Location) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

//==================================
/* Class Edge: merepresentasikan satu jalan/gang yang menghubungkan satu location ke location tujuan, 
   dengan bobot berupa waktu tempuh (dlm menit) */
//===================================
class Edge {
    private Location destination;
    private double weight; // waktu tempuh dalam menit

    public Edge(Location destination, double weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public Location getDestination() {
        return destination;
    }

    public double getWeight(){
        return weight;
    }

    @Override
    public String toString(){
        return "ke " + destination.getName() + " (" + weight + " menit)";
    }
}

//=====================================
//Class Graph: merepresentasikan peta sebagai graph berbobot
//menggunakan konsep Adjacency List.
//=====================================
class Graph {
    private Map<String, Location> locations;
    private Map<String, List<Edge>> adjacencyList;

    public Graph(){
        this.locations = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    // Kompleksitas Waktu: O(1) - operasi HashMap (put/containsKey) bersifat konstan.
    public void addLocation(String name) {
        if (!locations.containsKey(name)) {
            locations.put(name, new Location(name));
            adjacencyList.put(name, new ArrayList<>());
        }
    }

    // Kompleksitas Waktu: O(1) - addLocation, get, dan add ke ArrayList semuanya konstan.
    public void addEdge(String from, String to, double weightInMinutes) {
        addLocation(from);
        addLocation(to);
 
        Location fromLocation = locations.get(from);
        Location toLocation = locations.get(to);
 
        adjacencyList.get(from).add(new Edge(toLocation, weightInMinutes));
        adjacencyList.get(to).add(new Edge(fromLocation, weightInMinutes)); //dua arah
    }

    // Kompleksitas Waktu: O(1) - getOrDefault langsung ambil referensi list, tanpa loop.
    public List<Edge> getNeighbors(String locationName) {
        return adjacencyList.getOrDefault(locationName, new ArrayList<>());
    }

    // Kompleksitas Waktu: O(1) - pencarian berbasis key di HashMap.
    // Catatan: disediakan untuk kebutuhan integrasi, belum dipakai internal.
    public Location getLocation(String name) {
        return locations.get(name);
    }

    // Kompleksitas Waktu: O(1) - pengecekan key di HashMap.
    public boolean hasLocation(String name) {
        return locations.containsKey(name);
    }

    // Kompleksitas Waktu: O(1) untuk method ini sendiri (hanya return referensi).
    // Catatan: kalau hasilnya di-iterasi (lihat pemakaian di Dijkstra), iterasinya O(V).
    public Collection<Location> getAllLocations() {
        return locations.values();
    }

    // Kompleksitas Waktu: O(V + E) - setiap lokasi dan jalan ditampilkan tepat sekali.
    public void printMap() {
        System.out.println("=== Struktur Peta ===");
        for (String name : adjacencyList.keySet()) {
            System.out.print(name + " -> ");
            List<Edge> neighbors = adjacencyList.get(name);
            if (neighbors.isEmpty()) {
                System.out.println("(tidak ada jalan terhubung)");
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (Edge edge : neighbors) {
                sb.append(edge.toString()).append(", ");
            }
            String result = sb.toString();
            if (result.endsWith(", ")) {
                result = result.substring(0, result.length() - 2);
            }
            System.out.println(result);
        }
    }
}

//==================================================
// Class Dijkstra: mengimplementasikan algoritma Dijkstra untuk mencari rute tercepat
//==================================================
class Dijkstra {
    // Kelas untuk tracking node di PriorityQueue
    private static class NodePQ implements Comparable<NodePQ> {
        String name;
        double distance;

        public NodePQ(String name, double distance) {
            this.name = name;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodePQ other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    // Menyimmpan hasil pencarian rute: total waktu tempuh dan urutan lokasi yang dilalui
    public static class RouteResult {
        public double totalDistance;
        public List<String> path;
 
        public RouteResult(double totalDistance, List<String> path) {
            this.totalDistance = totalDistance;
            this.path = path;
        }
 
        public boolean isReachable() {
            return totalDistance != Double.POSITIVE_INFINITY;
        }
    }
    
    // Kompleksitas Waktu: O((V + E) log V).
    // Setiap lokasi diambil dari PriorityQueue maksimal sekali (poll = O(log V)),
    // dan setiap jalan diperiksa untuk relaksasi jarak (add = O(log V) jika update).
    public static RouteResult shortestPath (Graph graph, String start, String end) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
 
        // O(V) - Menginisialisasi semua jarak lokasi awal menjadi tak hingga
        for (Location loc : graph.getAllLocations()) {
            distances.put(loc.getName(), Double.POSITIVE_INFINITY);
        }
 
        if (!graph.hasLocation(start) || !graph.hasLocation(end)) {
            return new RouteResult(Double.POSITIVE_INFINITY, new ArrayList<>());
        }
 
        distances.put(start, 0.0);
        PriorityQueue<NodePQ> pq = new PriorityQueue<>();
        pq.add(new NodePQ(start, 0.0));
 
        while (!pq.isEmpty()) {
            NodePQ currentObj = pq.poll(); // O(log V) - Mengambil node jarak terdekat
            String current = currentObj.name;
            double currentDist = currentObj.distance;
 
            if (current.equals(end)) {
                break; 
            }
            
            if (currentDist > distances.get(current)) {
                continue;
            }

            // Memeriksa tetangga. Total seluruh perulangan ini di dalam while adalah O(E)
            for (Edge edge : graph.getNeighbors(current)) {
                String neighborName = edge.getDestination().getName();
                double newDist = distances.get(current) + edge.getWeight();
 
                if (newDist < distances.get(neighborName)) {
                    distances.put(neighborName, newDist);
                    previous.put(neighborName, current);
                    pq.add(new NodePQ(neighborName, newDist)); // O(log V) - Menaruh data diperbarui ke antrean
                }
            }
        }
 
        double finalDistance = distances.get(end);
        List<String> path = reconstructPath(previous, start, end, finalDistance);
 
        return new RouteResult(finalDistance, path);
    }

    // Kompleksitas Waktu: O(V) - backtrack dari end ke start, lalu reverse list.
    private static List<String> reconstructPath(Map<String, String> previous, String start, String end, double finalDistance) {
        List<String> path = new ArrayList<>();
        if (finalDistance == Double.POSITIVE_INFINITY) {
            return path; // tidak ada rute
        }
 
        String current = end;
        path.add(current);
        while (!current.equals(start)) {
            current = previous.get(current);
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }
}

//===============================================
// Class Utama MapGraph 
//===============================================
public class MapGraph {
    private Graph graph;

    public MapGraph() {
        this.graph = new Graph();
    }
    
    public void addLocation(String name) {
        graph.addLocation(name);
    }

    public void addEdge(String from, String to, double weightInMinutes) {
        graph.addEdge(from, to, weightInMinutes);
    }

    public List<Edge> getNeighbors(String locationName) {
        return graph.getNeighbors(locationName);
    }

    public void printMap() {
        graph.printMap();
    }

    // Kompleksitas Waktu: O((V + E) log V) - meneruskan ke Dijkstra.shortestPath.
    public Dijkstra.RouteResult findShortestPath(String start, String end) {
        return Dijkstra.shortestPath(graph, start, end);
    }
}
