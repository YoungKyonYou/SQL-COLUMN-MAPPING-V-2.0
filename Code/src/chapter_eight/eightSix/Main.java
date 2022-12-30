package chapter_eight.eightSix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static int ans =Integer.MAX_VALUE ;
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int[] coins = new int[N];
        int change = 0;

        st = new StringTokenizer(br.readLine());

        for(int i=0; i<N; i++){
            coins[i] = Integer.parseInt(st.nextToken());
        }

        st = new StringTokenizer(br.readLine());
        change = Integer.parseInt(st.nextToken());


        DFS(0, coins, N, change, 0, 0);
        System.out.println(ans);
    }

    public static void DFS(int L, int[] coins, int N, int change, int sum, int cnt){
        if(sum>change){
            return;
        }
        if(cnt>=ans){
            return;
        }
        if(sum==change){
            ans = Math.min(cnt, ans);
        }else{
            for(int i=0;i<coins.length;i++){
                DFS(L+1, coins, N, change, sum+coins[i], cnt+1);
            }
        }
    }
}
