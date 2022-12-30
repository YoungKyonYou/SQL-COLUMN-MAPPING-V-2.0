package chapter_seven.sevenEight;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class  Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int cnt =0 ;
        int max = 10001;

        boolean[] flag = new boolean[10001];
        int[] skyPongDis = {-1, 1, 5};

        int pPos = Integer.parseInt(st.nextToken());
        int gPos = Integer.parseInt(st.nextToken());

        Queue<Integer> queue = new LinkedList<>();
        flag[pPos] = true;
        queue.add(pPos);

        while(!queue.isEmpty()){
            int len = queue.size();
            for (int j = 0; j < len; j++) {
                int position = queue.poll();

                for (int i = 0; i <3 ; i++) {
                    int dis = position + skyPongDis[i];
                    if(position == gPos ){
                        System.out.println(cnt);
                        return;
                    }
                    if(dis > 0 && dis< 10001 && !flag[dis]){
                        queue.add(dis);
                        flag[dis]=true;
                    }
                }
            }
            cnt++;
        }
    }
}



















