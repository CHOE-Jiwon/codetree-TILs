import java.util.*;

public class Main {
    private static final int MAX_L = 70;

    private static int R, C, K; // 행, 열, 골렘의 개수를 의미합니다

    // 실제 숲을 [1~R][1~C]로 사용하기 위해 1만큼의 크기를 더 갖습니다. (one base indexing)
    // 골렘의 시작점은 숲 바깥이므로 실제 숲을 [4~R+3][1~C]로 사용하기위해 행은 3만큼의 크기를 더 갖습니다
    private static int[][] map = new int[MAX_L + 4][MAX_L + 1];
    private static boolean[][] isExit = new boolean[MAX_L + 4][MAX_L + 1]; // 해당 칸이 골렘의 출구인지 저장합니다

    private static int[] dr = {-1, 0, 1, 0};
    private static int[] dc = {0, 1, 0, -1};

    private static int answer = 0; // 각 정령들이 도달할 수 있는 최하단 행의 총합을 저장합니다

    // (r, c)가 숲의 범위 안에 있는지 확인하는 함수입니다
    private static boolean inRange(int r, int c) {
        return 4 <= r && r <= R + 3 && 1 <= c && c <= C;
    }

    // 숲에 있는 골렘들이 모두 빠져나갑니다
    private static void resetMap() {
        for (int i = 4; i <= R + 3; i++) {
            for (int j = 1; j <= C; j++) {
                map[i][j] = 0;
                isExit[i][j] = false;
            }
        }
    }

    // 골렘의 중심이 (r, c)에 위치할 수 있는지 확인합니다.
    // 북쪽에서 남쪽으로 내려와야하므로 중심이 (r, c)에 위치할때의 범위와 (r-1, c)에 위치할떄의 범위 모두 확인합니다
    // canGo의 쓰임을 보면 현재 좌표를 기준으로 다음 좌표를 인자로 넘김.
    // 즉, 내가 다음 좌표에 골렘을 배치할 수 있는지를 보는 것.
    // 좌, 우를 거치지 않고 내려오는 골렘의 경우 (r-1, c)를 보지 않아도 되지만
    // 봐도 결과는 같음.
    // 좌, 우를 거치고 내려오는 골렘의 경우 (r-1, c) 의 범위도 확인을 해야함.
    // 확인하는 범위 중 하나라도 빈 공간이 아닌 경우 갈 수가 없음.
    private static boolean canGo(int r, int c) {
        // 골렘의 현재 위치 기반으로 숲 안에 있는지 초기 세팅
        boolean flag = 1 <= c - 1 && c + 1 <= C && r + 1 <= R + 3;

        flag = flag && (map[r - 1][c - 1] == 0);
        flag = flag && (map[r - 1][c] == 0);
        flag = flag && (map[r - 1][c + 1] == 0);

        flag = flag && (map[r][c - 1] == 0);
        flag = flag && (map[r][c] == 0);
        flag = flag && (map[r][c + 1] == 0);
        flag = flag && (map[r + 1][c] == 0);

        return flag;
    }

    // 정령이 움직일 수 있는 모든 범위를 확인하고 도달할 수 있는 최하단 행을 반환합니다
    private static int bfs(int r, int c) {
        int result = r;
        Queue<int[]> q = new LinkedList<>();
        boolean[][] visit = new boolean[MAX_L + 4][MAX_L + 1];
        q.offer(new int[]{r, c});
        visit[r][c] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            for (int k = 0; k < 4; k++) {
                int nr = cur[0] + dr[k];
                int nc = cur[1] + dc[k];

                // 숲을 벗어나면 안됨
                if (!inRange(nr, nc)) continue;
                // 방문했던 곳이면 안됨
                if (visit[nr][nc]) continue;

                // 정령의 움직임은 같은 골렘 내부이거나
                if(map[nr][nc] == map[cur[0]][cur[1]]){
                    q.offer(new int[]{nr, nc});
                    visit[nr][nc] = true;
                    result = Math.max(result, nr);
                }
                // 골렘의 탈출구에 위치하고 있다면 다른 골렘으로 옮겨 갈 수 있습니다
                if(map[nr][nc] != 0 && isExit[cur[0]][cur[1]]) {
                    q.offer(new int[]{nr, nc});
                    visit[nr][nc] = true;
                    result = Math.max(result, nr);
                }
            }
        }

        return result;
    }

    // 골렘id가 중심 (y, x), 출구의 방향이 d일때 규칙에 따라 움직임을 취하는 함수입니다
    // 1. 남쪽으로 한 칸 내려갑니다.
    // 2. (1)의 방법으로 이동할 수 없으면 서쪽 방향으로 회전하면서 내려갑니다.
    // 3. (1)과 (2)의 방법으로 이동할 수 없으면 동쪽 방향으로 회전하면서 내려갑니다.
    private static void down(int r, int c, int d, int id) {
        if (canGo(r + 1, c)) {
            // 아래로 내려갈 수 있는 경우입니다
            down(r + 1, c, d, id);
        } else if (canGo(r + 1, c - 1)) {
            // 왼쪽 아래로 내려갈 수 있는 경우입니다
            down(r + 1, c - 1, (d + 3) % 4, id);
        } else if (canGo(r + 1, c + 1)) {
            // 오른쪽 아래로 내려갈 수 있는 경우입니다
            down(r + 1, c + 1, (d + 1) % 4, id);
        } else {
            // 1, 2, 3의 움직임을 모두 취할 수 없을떄 입니다.
            // 골렘을 3*3 격자로 씌운 후 좌상단과 우하단 점만 확인해서 숲을 벗어나면 안됨.
            if (!inRange(r - 1, c - 1) || !inRange(r + 1, c + 1)) {
                // 숲을 벗어나는 경우 모든 골렘이 숲을 빠져나갑니다
                resetMap();
            } else {
                // 골렘이 숲 안에 정착합니다
                map[r][c] = id;
                for (int k = 0; k < 4; k++)
                    map[r + dr[k]][c + dc[k]] = id;
                // 골렘의 출구를 기록하고
                isExit[r + dr[d]][c + dc[d]] = true;
                // bfs를 통해 정령이 최대로 내려갈 수 있는 행를 계산하여 누적합합니다
                answer += bfs(r, c) - 4 + 1;
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        R = sc.nextInt();
        C = sc.nextInt();
        K = sc.nextInt();

        for (int id = 1; id <= K; id++) { // 골렘 번호 id
            int startC = sc.nextInt();
            int exitD = sc.nextInt();
            down(1, startC, exitD, id);
        }
        System.out.println(answer);
    }
}