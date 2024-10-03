import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static final int MAX_NODE = 100000;
    static final int MAX_DEPTH = 100;


    // one-based indexing
    // zero node is dummy node
    static Node[] tree = new Node[MAX_NODE + 1];

    static {
        for (int i = 0; i <= MAX_NODE; i++) {
            tree[i] = new Node();
        }
    }

    static boolean[] isRoot = new boolean[MAX_NODE + 1];

    static class Node {
        int id;
        int parent;
        ColorInfo colorInfo = new ColorInfo();
        int maxDepth;
        ArrayList<Integer> childIds = new ArrayList<>();
    }

    static class ColorInfo {
        int color;
        int lastUpdate;
    }

    static void addNode(Scanner sc, int seq) {
        int mid = sc.nextInt();
        int pid = sc.nextInt();
        int color = sc.nextInt();
        int maxDepth = sc.nextInt();

        if (pid == -1) {
            isRoot[mid] = true;
        }

        if (isRoot[mid] || canMakeChild(tree[pid], 1)) {
            tree[mid].id = mid;
            tree[mid].parent = isRoot[mid] ? 0 : pid;
            tree[mid].colorInfo.color = color;
            tree[mid].colorInfo.lastUpdate = seq;
            tree[mid].maxDepth = maxDepth;

            if (!isRoot[mid]) {
                tree[pid].childIds.add(mid);
            }
        }
    }

    static boolean canMakeChild(Node parentNode, int requireDepth) {
        if (parentNode.id == 0) {
            return true;
        }

        if (parentNode.maxDepth <= requireDepth) {
            return false;
        }

        return canMakeChild(tree[parentNode.parent], requireDepth + 1);
    }

    static void changeColor(Scanner sc, int seq) {
        int mid = sc.nextInt();
        int color = sc.nextInt();

        tree[mid].colorInfo.color = color;
        tree[mid].colorInfo.lastUpdate = seq;
    }

    // 현재 노드를 기준으로 루트 노드까지 순회하면서, 해당 경로에서 가장 최신 색깔과 그 업데이트 시점을 알아야 함.
    // 해당 값을 자신의 업데이트 시점과 비교하여 최근 색깔을 보여주어야 함.
    // 그니까, 현재 노드의 업데이트 시점과 경로에 있는 가장 최근 시점을 비교하기 위해서는
    // "가장 최근 시점" 에 대한 정보가 필요하겠지? 이걸 내가 가져와야겠지? 따라서 return 타입에 이걸 하나 넣고
    // "가장 최근 시점" 에 색깔이 무엇인지도 알아야겠지? 그니까 Return 타입에 이걸 또 넣는 거지
    // 이걸 class 로 만들어서 한다면?
    static ColorInfo getColor(Node curr) {
        // 더 이상 업데이트 시점을 비교할 부모가 없음
        if (curr.parent == 0) {
            return curr.colorInfo;
        }

        // 업데이트 시점을 비교할 부모 노드의 색 정보 가져오기
        ColorInfo parentInfo = getColor(tree[curr.parent]);

        // 업데이트 시점 비교 후, 부모가 더 최근이면 부모의 색 정보 리턴
        if (parentInfo.lastUpdate > curr.colorInfo.lastUpdate) {
            return parentInfo;
        } else {
            return curr.colorInfo;
        }
    }

    static class ColorCount {
        int[] cnt = new int[6];

        ColorCount add(ColorCount obj) {
            ColorCount res = new ColorCount();

            for (int i = 1; i <= 5; i++) {
                res.cnt[i] = this.cnt[i] + obj.cnt[i];
            }

            return res;
        }

        int score() {
            int result = 0;
            for (int i = 1; i <= 5; i++) {
                if (this.cnt[i] > 0) result++;
            }

            return result * result;
        }
    }

    // 하향 탐색
    // getBeauty 같은 경우 무슨 함수일까?
    // 루트 노드의 트리에서 각 노드의 가치의 합을 구하는 함수
    // 가치의 합을 구하기 위해서는 뭐가 필요해?
    // 1. 현재 노드를 루트 노드로 하는 서브트리에서 서로 다른색이 몇개 있는지
    // 2. 서브트리의 각 노드들의 가치의 합
    // 1*1 + 2를 한 것이 하나의 결과가 되는 것.
    // 따라서 Object[] 라는 return type 에서 [0] 은 가치 합, [1] 은 색상 정보를 나타냄
    // 색상 정보를 최신 값으로 갱신하기 위해 하향식으로 내려갈 때 현재 최신 갱신 값이 뭔지 넘김
    // 최신 갱신 값이 현재 노드의 컬러 정보가 아닐 수도 있음. 따라서 인자로 컬러 정보는 꼭 넘겨주어야 함
    static Object[] getBeauty(Node curr, int latestColor, int lastUpdate) {
        // root에서부터 내려온 색 정보보다 현재 노드의 색 정보가 최신이라면 갱신
        if (lastUpdate < curr.colorInfo.lastUpdate) {
            lastUpdate = curr.colorInfo.lastUpdate;
            latestColor = curr.colorInfo.color;
        }

        int result = 0;

        ColorCount colorCount = new ColorCount();
        colorCount.cnt[latestColor] = 1;

        for (int childId : curr.childIds) {
            Node child = tree[childId];
            Object[] subResult = getBeauty(child, latestColor, lastUpdate);
            colorCount = colorCount.add((ColorCount) subResult[1]);
            result += (Integer) subResult[0];
        }

        result += colorCount.score();

        return new Object[]{result, colorCount};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1 <= Q <= 100,000
        int Q = sc.nextInt();

        for (int seq = 1; seq <= Q; seq++) {
            int command = sc.nextInt();

            if (command == 100) {
                addNode(sc, seq);
            } else if (command == 200) {
                changeColor(sc, seq);
            } else if (command == 300) {
                int mid = sc.nextInt();
                int color = getColor(tree[mid]).color;

                System.out.println(color);
            } else if (command == 400) {
                int answer = 0;

                for (int i = 1; i <= MAX_NODE; i++) {
                    if (isRoot[i]) {
                        Node root = tree[i];
                        answer += (Integer) getBeauty(root, root.colorInfo.color, root.colorInfo.lastUpdate)[0];
                    }
                }

                System.out.println(answer);
            }

        }
    }
}