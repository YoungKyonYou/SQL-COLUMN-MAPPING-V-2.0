package chapter_eight.eightTwo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static long ans=0;
    public static void main(String[] args) throws Exception{
        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        long C = Long.parseLong(st.nextToken());
        int N = Integer.parseInt(st.nextToken());

        long[] weight = new long[N];

        for(int i=0;i<N;i++){
            st=new StringTokenizer(br.readLine());
            weight[i] = Long.parseLong(st.nextToken());
        }

        boolean[] flag = new boolean[N+1];
        DFS(0,weight, flag, C, 0, N);
        System.out.println(ans);
    }

    public static void DFS( long sum, long[] weight, boolean[] flag, long limit, int L, int N){
        if(sum > limit)
            return;
        if(L == N ){
            ans = Math.max(ans, sum);
        }else{
            DFS(sum+weight[L], weight, flag, limit, L+1,N);
            DFS(sum, weight, flag, limit, L+1, N);
        }
    }
}
