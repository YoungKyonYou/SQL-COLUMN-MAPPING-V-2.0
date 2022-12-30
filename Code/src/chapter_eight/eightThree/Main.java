package chapter_eight.eightThree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static int ans =0 ;
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        //개수
        int N = Integer.parseInt(st.nextToken());
        //제한시간
        int M = Integer.parseInt(st.nextToken());

        int[][] scores = new int[N+1][2];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            //점수
            scores[i][0] = Integer.parseInt(st.nextToken());
            //시간
            scores[i][1] = Integer.parseInt(st.nextToken());
        }

        DFS(scores, 0, M, N, 0,0);
        System.out.println(ans);
    }
    public static void DFS(int[][] scores, int L, int limitTime, int N, int scoreSum, int timeSum){

        if(limitTime<timeSum){
            return;
        }
        if(L==N){
            ans = Math.max(scoreSum, ans);
        }else{
            DFS(scores, L+1, limitTime, N, scoreSum+scores[L][0], timeSum+scores[L][1]);
            DFS(scores, L+1, limitTime, N, scoreSum, timeSum);
        }
    }
}
