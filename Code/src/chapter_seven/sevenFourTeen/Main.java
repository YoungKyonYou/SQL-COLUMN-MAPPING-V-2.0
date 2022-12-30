package chapter_seven.sevenFourTeen;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static List<Integer>[] graph = new List[21];

    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int X = Integer.parseInt(st.nextToken());

        int[] distance = new int[N+1];
        Arrays.fill(graph, new ArrayList<>());

        for(int i=1; i<=N ;i++){
            graph[i] = new ArrayList<>();
        }

        for (int i = 0; i < X; i++) {

            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            graph[from].add(to);
        }

        for (int i = 0; i < N; i++) {
            int ans = BFS(1,(i+1), distance);
            System.out.println((i+1)+":"+ans);
            Arrays.fill(distance, 0);
        }

    }

    public static int BFS(int start, int end, int[] distance){
        Queue<Integer> queue = new LinkedList<>();

        int cnt=0;

        queue.add(start);
        distance[start] = 1;

        while(!queue.isEmpty()){
            int len = queue.size();
            for (int i = 0; i < len; i++) {
                int k = queue.poll();
                for (int g : graph[k]) {
                    if(distance[g] == 0){
                        distance[g] = 1;
                        queue.add(g);
                    }
                    if(distance[end] == 1){
                        return cnt+1;
                    }

                }
            }
            cnt++;
        }
        return cnt;
    }
}











