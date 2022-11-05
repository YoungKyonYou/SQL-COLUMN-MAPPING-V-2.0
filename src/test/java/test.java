import org.junit.jupiter.api.Test;

public class test {
    @Test
    public void testRegex(){
        String s = "dsdf123.test ";
        System.out.println("tesT:"+s.replaceFirst("[a-zA-Z0-9.]"+"test", "dsdf123"+"hello"));
    }
}
