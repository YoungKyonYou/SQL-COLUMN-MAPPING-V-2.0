package chapter_seven.sevenNine;
import java.util.*;
public class Main {
    static int answer =0 ;
    public static void main(String[] args) {
        boolean[] flag = new boolean[100];
        List<Integer>[] nodes = new List[10];

        List<Integer> node1 = new ArrayList<>();
        nodes[1] = node1;
        node1.add(2);
        node1.add(3);

        List<Integer> node2 = new ArrayList<>();
        nodes[2] = node2;
        node2.add(1);
        node2.add(4);
        node2.add(5);

        List<Integer> node3 = new ArrayList<>();
        nodes[3] = node3;
        node3.add(1);

        List<Integer> node4 = new ArrayList<>();
        nodes[4] = node4;
        node4.add(2);

        List<Integer> node5 = new ArrayList<>();
        nodes[5] = node5;
        node5.add(2);
        flag[1] = true;
        int result = DFS(nodes,node1, 4, flag, 1);
        System.out.println(answer);


    }

    private static int DFS(List<Integer>[] nodes, List<Integer> node1, int target, boolean[] flag, int cnt) {
        for (Integer n : node1) {
            if(n == target){
                return answer=cnt;
            }else if(!flag[n]){
                flag[n]=true;
                DFS(nodes, nodes[n], target, flag, cnt+1);
            }
        }
        return cnt;
    }

}
