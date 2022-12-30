import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int num = Integer.parseInt(st.nextToken());
        printNum(1, num);
    }

    public static void printNum(int num, int limit){
        System.out.print(num+" ");
        if(limit == num){
            return;
        }
        printNum(num+1, limit);
    }

}
