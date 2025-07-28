package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.StatPage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CardBuilder {
    private static final Paint mainLightModePaint = new GradientPaint(0, 0, new Color(0xf5f5f5), 1500, 750, new Color(0xcacccf));
    private static final Paint dualLightModePaint = new GradientPaint(0, 0, new Color(0x804b5054), 1500, 750, new Color(0x802a2c2e));
    private static final Font font;
    
    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("Lato-Regular.ttf"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static BufferedImage createCard(Build build) {
        StatPage statPage = StatPage.calculateStats(build);
        BufferedImage output = new BufferedImage(1500, 750, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setPaint(mainLightModePaint);
        g2d.fillRect(0, 0, 1500, 750);
        BufferedImage cachedImage = build.character.getImage();
        g2d.drawImage(cachedImage.getScaledInstance(-1,750, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.setFont(font.deriveFont(Font.PLAIN, 80));
        int characterNameWidth = g2d.getFontMetrics().stringWidth(build.character.getName()) + 10;
        g2d.setPaint(dualLightModePaint);
        g2d.fillRect(0, 0, (int) Math.floor(characterNameWidth), 100);
        g2d.fillRect((int) Math.floor(characterNameWidth), 0, 10, 90);
        g2d.fillArc((int) Math.floor(characterNameWidth) - 10, 80, 20, 20, 0, -90);
        g2d.setPaint(mainLightModePaint);
        g2d.drawString(build.character.getName(), 10, 80);
        g2d.dispose();
        return output;
    }
}
