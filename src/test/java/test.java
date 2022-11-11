import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class test {
    @Test
    public void testRegex() {

        int width = 100;
        int height = 30;
        String str = "";

        //BufferedImage image = ImageIO.read(new File("/Users/mkyong/Desktop/logo.jpg"));
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(new Font("Dialog", Font.PLAIN, 20));

        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.drawString("AsianaIDT", 1, 20);

        //save this image
        //ImageIO.write(image, "png", new File("/users/mkyong/ascii-art.png"));

        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {

                sb.append(image.getRGB(x, y) == -16777216 ? " " : "$");

            }
            str+="\n";

            if (sb.toString().trim().isEmpty()) {
                continue;
            }
            str+=sb.toString();

           // System.out.println(sb);
        }
        System.out.println(str);

    }

    @Test
    public void test3(){


        String str="tb_wbwr_trspbill_c        a,                                                              \n" +
                "         tb_wbws_subbill_c         aa,                                                             \n" +
                "         nexs.tb_wbws_spbillprgs_c      aaa,                                                            \n" +
                "         nexs.tb_wbwr_clntinfo_c        aaaa,                                                           \n" +
                "         nexs.tb_stst_trspbilllaststs_d d,                                                              \n" +
                "         tb_mscd_code_d            c ";
        String[] strs = {"tb_wbws_spbillprgs_c", "tb_wbwr_clntinfo_c", "tb_stst_trspbilllaststs_d"};
        for(int i=0;i<strs.length;i++){
            str= str.replaceAll("\\b[a-zA-Z0-9]+."+strs[i]+"\\b",strs[i]);
        }
        for(String s: strs){
            System.out.println(s);
        }
    }

}
