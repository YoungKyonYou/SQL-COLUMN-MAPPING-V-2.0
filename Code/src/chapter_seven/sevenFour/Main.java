package chapter_seven.sevenFour;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    private static int arr[] = new int[200];
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int num = Integer.parseInt(st.nextToken());
        for (int i = 1; i <=num; i++) {
            System.out.print(dfs(i)+" ");
        }
    }

    public static int dfs(int num){
        if(arr[num] != 0){
            return arr[num];
        }else if(num==1 || num==2){
            return arr[num]=1;
        }else{
            return arr[num] = dfs(num-2)+dfs(num-1);
        }
    }

}
