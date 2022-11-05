import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class test {
    @Test
    public void testRegex(){
        BufferedImage bufferedImage = new BufferedImage(
                100, 100,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.drawString("BAELDUNG", 12, 24);


    }

}
