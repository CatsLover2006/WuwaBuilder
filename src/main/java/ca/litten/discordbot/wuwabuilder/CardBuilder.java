package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.StatPage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CardBuilder {
    private static final Paint mainLightModePaint = new GradientPaint(0, 0, new Color(0xf5f5f5), 1500, 750, new Color(0xcacccf));
    private static final Paint dualLightModePaint = new GradientPaint(0, 0, new Color(0xc02a2c2e, true), 1500, 750, new Color(0xc0000000, true));
    private static final Font font;
    
    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("res/Lato-Regular.ttf"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private final boolean isLightMode;
    
    public CardBuilder() {
        isLightMode = false;
    }
    
    public CardBuilder(boolean isLightMode) {
        this.isLightMode = isLightMode;
    }
    
    public void drawCircledImage(Graphics2D g2d, int x, int y, int r, BufferedImage image, Color front, Color back) {
        g2d.setColor(back);
        g2d.drawArc(x, y, r * 2, r * 2, 0, 360);
        BufferedImage imageTint = new BufferedImage(r, r, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3d = imageTint.createGraphics();
        g3d.drawImage(image.getScaledInstance(r, r, Image.SCALE_SMOOTH), 0, 0, null);
        g3d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, front.getAlpha() / 255.0f));
        g3d.setColor(front);
        g3d.fillRect(0, 0, r, r);
        g3d.dispose();
        g2d.drawImage(imageTint, x, y, null);
    }
    
    public BufferedImage createCard(Build build) {
        StatPage statPage = StatPage.calculateStats(build);
        BufferedImage output = new BufferedImage(1500, 750, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setPaint(mainLightModePaint);
        g2d.fillRect(0, 0, 1500, 750);
        BufferedImage cachedImage = build.character.getImage();
        g2d.drawImage(cachedImage.getScaledInstance(750,-1, Image.SCALE_SMOOTH), 0, -100, null);
        g2d.setPaint(dualLightModePaint);
        g2d.fillRect(750, 0, 750, 750);
        int characterNameHeight = 110;
        int characterNameWidth = Integer.MAX_VALUE;
        while (characterNameWidth > 500) {
            characterNameHeight -= 10;
            g2d.setFont(font.deriveFont(Font.PLAIN, (int)(characterNameHeight * 0.9)));
            characterNameWidth = g2d.getFontMetrics().stringWidth(build.character.getName());
        }
        System.out.println(characterNameHeight);
        int characterPositionX = 375 - characterNameWidth/2;
        g2d.fillRect(characterPositionX, 0, characterNameWidth, characterNameHeight);
        g2d.fillRect(characterPositionX + characterNameWidth, 0, characterNameHeight/10, (int)(characterNameHeight * 0.9));
        g2d.fillRect(characterPositionX - characterNameHeight/10, 0, characterNameHeight/10, (int)(characterNameHeight * 0.9));
        g2d.fillArc(characterPositionX + characterNameWidth - characterNameHeight/10, (int)(characterNameHeight * 0.8), characterNameHeight/5, characterNameHeight/5, 0, -90);
        g2d.fillArc(characterPositionX - characterNameHeight/10, (int)(characterNameHeight * 0.8), characterNameHeight/5, characterNameHeight/5, 180, 90);
        g2d.setPaint(mainLightModePaint);
        g2d.fillRect(875, 0, 10, 750);
        g2d.drawString(build.character.getName(), characterPositionX, (int)(0.8 * characterNameHeight));
        int characterLevelHeight = 35;
        g2d.setFont(font.deriveFont(Font.PLAIN, (int)(characterLevelHeight * 0.9)));
        String characterLevelText = build.characterLevel.toString();
        switch (characterLevelText.charAt(0)) {
            case 'b':
                characterLevelText = characterLevelText + "/40";
                break;
            case 'c':
                characterLevelText = characterLevelText + "/50";
                break;
            case 'd':
                characterLevelText = characterLevelText + "/60";
                break;
            case 'e':
                characterLevelText = characterLevelText + "/70";
                break;
            case 'f':
                characterLevelText = characterLevelText + "/80";
                break;
            case 'g':
                characterLevelText = characterLevelText + "/90";
                break;
            case 'a':
            default:
                characterLevelText = characterLevelText + "/20";
                break;
        }
        characterLevelText = "Lv. " + characterLevelText.substring(1);
        int characterLevelWidth = g2d.getFontMetrics().stringWidth(characterLevelText);
        System.out.println(characterLevelWidth);
        characterPositionX = 375 - characterLevelWidth/2;
        g2d.setPaint(dualLightModePaint);
        g2d.fillRect(characterPositionX, characterNameHeight, characterLevelWidth, characterLevelHeight);
        g2d.fillRect(characterPositionX + characterLevelWidth, characterNameHeight, characterLevelHeight/10, (int)(characterLevelHeight * 0.9));
        g2d.fillRect(characterPositionX - characterLevelHeight/10, characterNameHeight, characterLevelHeight/10, (int)(characterLevelHeight * 0.9));
        g2d.fillArc(characterPositionX + characterLevelWidth - characterLevelHeight/10, characterNameHeight + (int)(characterLevelHeight * 0.8), characterLevelHeight/5, characterLevelHeight/5, 0, -90);
        g2d.fillArc(characterPositionX - characterLevelHeight/10, characterNameHeight + (int)(characterLevelHeight * 0.8), characterLevelHeight/5, characterLevelHeight/5, 180, 90);
        g2d.setPaint(mainLightModePaint);
        g2d.drawString(characterLevelText, characterPositionX, characterNameHeight + (int)(0.8 * characterLevelHeight));
        for (int i = 0; i < 6; i++) {
            drawCircledImage(g2d, 762, 175 * i + 12, 101, build.character.getChain(i),
                    new Color(0x80151617, build.chainLength > i), new Color(0xffffff));
        }
        g2d.dispose();
        return output;
    }
}
