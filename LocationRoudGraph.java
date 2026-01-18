import java.util.*;

public class LocationRoudGraph {
    private HashMap<String, HashMap<String, Integer>> adjacencyList;
    
    public LocationRoudGraph() {
        this.adjacencyList = new HashMap<>();
    }
    
    // Add a location (vertex)
    public void addLocation(String location) {
        adjacencyList.putIfAbsent(location, new HashMap<>());
    }
    
    // Add a route between two locations with distance (edge)
    public void addRoute(String from, String to, int distance) {
        adjacencyList.putIfAbsent(from, new HashMap<>());
        adjacencyList.putIfAbsent(to, new HashMap<>());
        adjacencyList.get(from).put(to, distance);
        adjacencyList.get(to).put(from, distance); // Bidirectional
    }
    
    // Get all locations
    public Set<String> getLocations() {
        return adjacencyList.keySet();
    }
    
    // Get neighbors of a location
    public HashMap<String, Integer> getNeighbors(String location) {
        return adjacencyList.getOrDefault(location, new HashMap<>());
    }
    
    // ALGORITHM 1: DIJKSTRA'S ALGORITHM - Shortest path from source to all locations
    public HashMap<String, ShortestPathResult> dijkstra(String source) {
        HashMap<String, Integer> distances = new HashMap<>();
        HashMap<String, String> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Set<String> visited = new HashSet<>();
        
        // Initialize distances
        for (String location : adjacencyList.keySet()) {
            distances.put(location, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        pq.add(new Node(source, 0));
        
        System.out.println("\n=== DIJKSTRA'S ALGORITHM PROCESSING ===");
        System.out.println("Starting from: " + source);
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentLocation = current.location;
            
            if (visited.contains(currentLocation)) continue;
            visited.add(currentLocation);
            
            System.out.println("\nVisiting: " + currentLocation + " (Distance: " + current.distance + ")");
            
            HashMap<String, Integer> neighbors = adjacencyList.get(currentLocation);
            if (neighbors != null) {
                for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                    String neighborLocation = neighbor.getKey();
                    int edgeWeight = neighbor.getValue();
                    int newDistance = distances.get(currentLocation) + edgeWeight;
                    
                    if (newDistance < distances.get(neighborLocation)) {
                        distances.put(neighborLocation, newDistance);
                        previous.put(neighborLocation, currentLocation);
                        pq.add(new Node(neighborLocation, newDistance));
                        System.out.println("  → Updated " + neighborLocation + ": " + newDistance + " km");
                    }
                }
            }
        }
        
        // Build results
        HashMap<String, ShortestPathResult> results = new HashMap<>();
        for (String destination : adjacencyList.keySet()) {
            List<String> path = buildPath(previous, source, destination);
            results.put(destination, new ShortestPathResult(
                distances.get(destination),
                path,
                "Dijkstra's Algorithm"
            ));
        }
        
        return results;
    }
    
    // ALGORITHM 2: BELLMAN-FORD ALGORITHM - Handles negative weights, detects negative cycles
    public HashMap<String, ShortestPathResult> bellmanFord(String source) {
        HashMap<String, Integer> distances = new HashMap<>();
        HashMap<String, String> previous = new HashMap<>();
        List<Edge> edges = new ArrayList<>();
        
        // Initialize distances
        for (String location : adjacencyList.keySet()) {
            distances.put(location, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        
        // Build edge list
        for (String from : adjacencyList.keySet()) {
            for (Map.Entry<String, Integer> entry : adjacencyList.get(from).entrySet()) {
                edges.add(new Edge(from, entry.getKey(), entry.getValue()));
            }
        }
        
        System.out.println("\n=== BELLMAN-FORD ALGORITHM PROCESSING ===");
        System.out.println("Starting from: " + source);
        System.out.println("Total edges to process: " + edges.size());
        
        // Relax edges V-1 times
        int iterations = adjacencyList.size() - 1;
        for (int i = 0; i < iterations; i++) {
            System.out.println("\nIteration " + (i + 1) + ":");
            boolean updated = false;
            
            for (Edge edge : edges) {
                if (distances.get(edge.from) != Integer.MAX_VALUE) {
                    int newDistance = distances.get(edge.from) + edge.weight;
                    if (newDistance < distances.get(edge.to)) {
                        distances.put(edge.to, newDistance);
                        previous.put(edge.to, edge.from);
                        System.out.println("  Updated " + edge.to + ": " + newDistance + " km (via " + edge.from + ")");
                        updated = true;
                    }
                }
            }
            
            if (!updated) {
                System.out.println("  No updates - Algorithm converged early!");
                break;
            }
        }
        
        // Check for negative cycles
        for (Edge edge : edges) {
            if (distances.get(edge.from) != Integer.MAX_VALUE) {
                int newDistance = distances.get(edge.from) + edge.weight;
                if (newDistance < distances.get(edge.to)) {
                    System.out.println("\n⚠ Warning: Negative cycle detected!");
                    return null;
                }
            }
        }
        
        // Build results
        HashMap<String, ShortestPathResult> results = new HashMap<>();
        for (String destination : adjacencyList.keySet()) {
            List<String> path = buildPath(previous, source, destination);
            results.put(destination, new ShortestPathResult(
                distances.get(destination),
                path,
                "Bellman-Ford Algorithm"
            ));
        }
        
        return results;
    }
    
    // Build path from source to destination
    private List<String> buildPath(HashMap<String, String> previous, String source, String destination) {
        List<String> path = new ArrayList<>();
        String current = destination;
        
        while (current != null) {
            path.add(0, current);
            if (current.equals(source)) break;
            current = previous.get(current);
        }
        
        return path.isEmpty() || !path.get(0).equals(source) ? new ArrayList<>() : path;
    }
    
    // Display graph
    public void displayGraph() {
        System.out.println("\n=== LOCATION NETWORK (GRAPH) ===");
        for (String location : adjacencyList.keySet()) {
            System.out.print(location + " → ");
            HashMap<String, Integer> neighbors = adjacencyList.get(location);
            for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                System.out.print(entry.getKey() + "(" + entry.getValue() + "km) ");
            }
            System.out.println();
        }
    }
    
    // Inner classes
    private static class Node {
        String location;
        int distance;
        
        Node(String location, int distance) {
            this.location = location;
            this.distance = distance;
        }
    }
    
    private static class Edge {
        String from;
        String to;
        int weight;
        
        Edge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}

// Result class for shortest path
class ShortestPathResult {
    int distance;
    List<String> path;
    String algorithm;
    
    public ShortestPathResult(int distance, List<String> path, String algorithm) {
        this.distance = distance;
        this.path = path;
        this.algorithm = algorithm;
    }
    
    public int getDistance() {
        return distance;
    }
    
    public List<String> getPath() {
        return path;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void display() {
        if (distance == Integer.MAX_VALUE) {
            System.out.println("No path available");
        } else {
            System.out.println("Distance: " + distance + " km");
            System.out.println("Path: " + String.join(" → ", path));
            System.out.println("Algorithm: " + algorithm);
        }
    }
}
