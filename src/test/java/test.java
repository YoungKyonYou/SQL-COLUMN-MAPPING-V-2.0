import org.junit.jupiter.api.Test;

public class test {
    @Test
    public void testRegex(){
        String s = "234sdf ";
        System.out.println("tesT:"+s.matches("[a-zA-Z0-9]+"));
    }
}
