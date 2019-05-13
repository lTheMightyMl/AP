import java.util.ArrayList;
import java.util.Scanner;

class Vertex {
    ArrayList<Vertex> adjacencyList = new ArrayList<Vertex>();
    boolean marked = false;
    long cost;
}

public class P3 {
    static ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    static long currentMaxC;

    static void dfs(Vertex currentVertex) {
        currentVertex.marked = true;
        currentMaxC = Long.min(currentMaxC, currentVertex.cost);
        for (Vertex child : currentVertex.adjacencyList) {
            if (!child.marked)
                dfs(child);
        }
        return;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            int c = scanner.nextInt();
            Vertex newVertex = new Vertex();
            newVertex.cost = c;
            vertices.add(newVertex);
        }
        for (int i = 0; i < m; i++) {
            int currentVertexNumber = scanner.nextInt();
            int childNumber = scanner.nextInt();
            currentVertexNumber--;
            childNumber--;
            Vertex currentVertex = vertices.get(currentVertexNumber);
            Vertex child = vertices.get(childNumber);
            currentVertex.adjacencyList.add(child);
            child.adjacencyList.add(currentVertex);
        }
        long answer = 0;
        for (int i = 0; i < n; i++) {
            Vertex currentVertex = vertices.get(i);
            if (!currentVertex.marked) {
                currentMaxC = 1000 * 1000 * 1000 + 1;
                dfs(currentVertex);
                answer += currentMaxC;
            }
        }
        System.out.println(answer);
        scanner.close();
        return;
    }
}