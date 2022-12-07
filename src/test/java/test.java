import org.apache.poi.ss.usermodel.Table;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        String tableNm = "tableNm";
        String sql = "ㅣ nexs.tableNm ㅣ nexs.tableNm nexs.tableNm   " +
                "nexs.tableNm nexs.tableNm sdfsfwe nexs.tableNm wre nexs.tableNm";
        sql = sql.replaceAll("\\b[a-zA-Z0-9]+\\." + tableNm + "\\b", tableNm);
        System.out.println("안뇽:"+sql);


    }

}
