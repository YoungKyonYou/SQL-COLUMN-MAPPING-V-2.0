package chapter_eight.eightFour;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        //자연수 N
        int N = Integer.parseInt(st.nextToken());
        // M번 뽑는다.
        int M = Integer.parseInt(st.nextToken());

        int[] repeat = new int[M];


        DFS(repeat, N, M,0);
    }

    public static void DFS(int[] repeat, int N, int M, int L){
        if(L == M){
            String temp = "";
            for(int x : repeat){
                temp+=(x+" ");
            }
            System.out.println(temp);
        }else{
            for(int i=1;i<=N;i++){
                repeat[L] = i;
                DFS(repeat, N, M, L+1);
            }
        }
    }
}
