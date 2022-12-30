package chapter_seven.sevenTwelve;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.*;

public class Main {
    private static int cnt=0;
    private static int maxNodeNum = 21;
    private static List<Integer>[] graph = new List[maxNodeNum];
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int end = Integer.parseInt(st.nextToken());
        int N = Integer.parseInt(st.nextToken());
        boolean[] flag = new boolean[6];


        for (int i = 1; i <maxNodeNum; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i = 0; i <N; i++) {
            st = new StringTokenizer(br.readLine());

            int grp = Integer.parseInt(st.nextToken());
            int node = Integer.parseInt(st.nextToken());
            graph[grp].add(node);
        }
        flag[1] = true;
        DFS(1, graph[1], end, flag);
        System.out.println(cnt);
    }

    private static void DFS(int start, List<Integer> node, int end, boolean[] flag) {
        if(start == end){
            cnt++;
        }else{
            for(Integer n : node){
                if(!flag[n]){
                    flag[n] = true;
                    DFS(n, graph[n], end, flag);
                    flag[n] = false;
                }
            }
        }
    }
}
