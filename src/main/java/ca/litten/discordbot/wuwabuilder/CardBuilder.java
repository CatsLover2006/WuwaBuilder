package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.StatPage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CardBuilder {
    private static final Paint bgLightModePaint = new GradientPaint(0, 0, new Color(0xf5f5f5), 1758, 732, new Color(0xcacccf));
    
    public static BufferedImage createCard(Build build) {
        StatPage statPage = StatPage.calculateStats(build);
        BufferedImage output = new BufferedImage(1758, 732, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setPaint(bgLightModePaint);
        g2d.fillRect(0, 0, 1758, 732);
        BufferedImage cachedImage = build.character.getImage();
        g2d.drawImage(cachedImage.getScaledInstance(-1,732, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();
        return output;
    }
}
