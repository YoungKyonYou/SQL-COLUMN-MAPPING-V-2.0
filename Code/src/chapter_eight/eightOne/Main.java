package chapter_eight.eightOne;


import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
    static List<Integer> set =new ArrayList<>();
    static boolean isAns = false;
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());

        boolean[] flag = new boolean[100];

        int sum = 0;

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < N; i++) {
            int num = Integer.parseInt(st.nextToken());
            sum += num;
            set.add(num);
        }

        DFS(flag, 0, N);

        if (isAns == true) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
    }

    static void DFS(boolean[] flag, int L, int N){
        if(L+1 == N){
            int sum1 =0 ;
            int sum2 =0 ;
            for(int i=0; i<N;i++){
                if(flag[i]){
                    sum1+=set.get(i);
                }else{
                    sum2+=set.get(i);
                }
            }
            if(sum1 == sum2){
                isAns = true;
                return;
            }
        }else{
            flag[L] = true;
            DFS(flag, L+1, N);
            flag[L] = false;
            DFS(flag, L+1, N);
        }
    }
}
