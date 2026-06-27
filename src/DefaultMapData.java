public class DefaultMapData {
    public static void load(MapGraph map) {
        //=== Restoran Lama ke Gang Utama ===
        map.addEdge("Resto Kenanga Asri", "Gang Sawo", 4.0);
        map.addEdge("Resto Pondok Mertua", "Gang Melati", 6.0);
        map.addEdge("Resto Graha Mawar", "Gang Cempaka", 5.0);
        map.addEdge("Resto Ayam Geprek", "Blok Akasia 4", 2.0);
        map.addEdge("Resto Ayam Geprek", "Blok Akasia 3", 4.0);
        map.addEdge("Resto Bebek Slamet", "Blok Cendana 4", 3.0);
        map.addEdge("Resto Bebek Slamet", "Gang Kamboja", 5.0);
        map.addEdge("Resto Warmindo Selera", "Blok Cendana 1", 2.5);
        map.addEdge("Resto Warmindo Selera", "Gang Sawo", 3.5);

        //=== Jaringan Gang Utama ===
        map.addEdge("Gang Sawo", "Gang Melati", 3.0);
        map.addEdge("Gang Melati", "Gang Kamboja", 2.0);
        map.addEdge("Gang Kamboja", "Gang Anggrek", 4.0);
        map.addEdge("Gang Anggrek", "Gang Cempaka", 3.0);
        map.addEdge("Gang Sawo", "Gang Kamboja", 6.0);
        map.addEdge("Gang Melati", "Gang Anggrek", 5.0);
        map.addEdge("Gang Cempaka", "Gang Sawo", 9.0);

        //=== Gang ke Blok Rumah Pelanggan ===
        map.addEdge("Gang Sawo", "Blok Cendana 1", 3.0);
        map.addEdge("Gang Sawo", "Blok Cendana 2", 5.0);
        map.addEdge("Gang Melati", "Blok Cendana 2", 2.0);
        map.addEdge("Gang Melati", "Blok Cendana 3", 4.0);
        map.addEdge("Gang Kamboja", "Blok Cendana 3", 3.0);
        map.addEdge("Gang Kamboja", "Blok Cendana 4", 6.0);
        map.addEdge("Gang Anggrek", "Blok Akasia 1", 4.0);
        map.addEdge("Gang Anggrek", "Blok Akasia 2", 3.0);
        map.addEdge("Gang Cempaka", "Blok Akasia 3", 2.0);
        map.addEdge("Gang Cempaka", "Blok Akasia 4", 5.0);

        //=== Jalan Pintas Antar Blok ===
        map.addEdge("Blok Cendana 2", "Blok Cendana 3", 2.0);
        map.addEdge("Blok Akasia 1", "Blok Akasia 2", 2.0);
        map.addEdge("Blok Akasia 3", "Blok Akasia 4", 3.0);
        map.addEdge("Blok Cendana 4", "Blok Akasia 1", 7.0);
    }
}
