package chapter_seven.sevenThree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st=new StringTokenizer(br.readLine());
        int num = Integer.parseInt(st.nextToken());
        int result = dfs(num);
        System.out.println("result:"+result);
    }

    public static int dfs(int num){
        if(num == 1){
            return 1;
        }else{
           return num*dfs(num-1);
        }
    }

}
