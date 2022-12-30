package chapter_seven.sevenTwo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st=new StringTokenizer(br.readLine());
        int num = Integer.parseInt(st.nextToken());
        dfs(num);
    }

    public static void dfs(int num){
        if(num == 0){
            return;
        }else{
            dfs(num/2);
            System.out.print((num%2));
        }
    }

}
