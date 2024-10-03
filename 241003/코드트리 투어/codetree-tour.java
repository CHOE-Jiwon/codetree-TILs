import java.util.*;

public class Main {
    final static int INF = Integer.MAX_VALUE;
    final static int MAX_N = 2000;
    final static int MAX_ID = 30005;

    static int N, M;

    static int[][] A = new int[MAX_N][MAX_N];
    static int[] D = new int[MAX_N];
    static boolean[] isMade = new boolean[MAX_ID];
    static boolean[] isCancel = new boolean[MAX_ID];

    static int S;

    static class Package implements Comparable<Package> {
        int id;
        int revenue;
        int dest;
        int profit;

        public Package(int id, int revenue, int dest, int profit) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.profit = profit;
        }

        @Override
        public int compareTo(Package other) {
            if (this.profit == other.profit) {
                return Integer.compare(this.id, other.id);
            }
            return Integer.compare(other.profit, this.profit);
        }
    }

    static PriorityQueue<Package> pq = new PriorityQueue<>();

    // dijkstra 알고리즘을 통해 시작도시 S에서 각 도시로 가는 최단거리를 구합니다.
    static void dijkstra() {
        boolean[] visit = new boolean[N];
        Arrays.fill(D, INF);
        D[S] = 0;

        for (int i = 0; i < N - 1; i++) {
            int v = 0, minDist = INF;
            for (int j = 0; j < N; j++) {
                if (!visit[j] && minDist > D[j]) {
                    v = j;
                    minDist = D[j];
                }
            }
            visit[v] = true;
            for (int j = 0; j < N; j++) {
                if (!visit[j] && D[v] != INF && A[v][j] != INF && D[j] > D[v] + A[v][j]) {
                    D[j] = D[v] + A[v][j];
                }
            }
        }
    }

    static void buildLand(Scanner sc) {
        N = sc.nextInt();
        M = sc.nextInt();

        for (int i = 0; i < N; i++) {
            Arrays.fill(A[i], INF);
            A[i][i] = 0;
        }

        for (int i = 0; i < M; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();

            A[u][v] = Math.min(A[u][v], w);
            A[v][u] = Math.min(A[v][u], w);
        }
    }

    static void addPackage(int id, int revenue, int dest) {
        isMade[id] = true;
        int profit = revenue - D[dest];
        pq.offer(new Package(id, revenue, dest, profit));
    }

    static void cancelPackage(int id) {
        if (isMade[id]) isCancel[id] = true;
    }

    static int sellPackage() {
        while (!pq.isEmpty()) {
            Package now = pq.peek();

            if (now.profit < 0) break;

            pq.poll();
            if (!isCancel[now.id]) return now.id;
        }

        return -1;
    }

    static void changeStart(Scanner sc) {
        S = sc.nextInt();

        dijkstra();

        List<Package> packages = new ArrayList<>();
        while (!pq.isEmpty()) {
            packages.add(pq.poll());
        }
        for (Package p : packages) {
            addPackage(p.id, p.revenue, p.dest);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int q = sc.nextInt();

        while (q-- > 0) {
            int command = sc.nextInt();

            if (command == 100 ) {
                buildLand(sc);
                dijkstra();
            } else if (command == 200) {
                int id = sc.nextInt();
                int revenue = sc.nextInt();
                int dest = sc.nextInt();

                addPackage(id, revenue, dest);
            } else if (command == 300) {
                int id = sc.nextInt();
                cancelPackage(id);
            } else if (command == 400) {
                int result = sellPackage();
                System.out.println(result);
            } else {
                changeStart(sc);
            }
        }
    }

}