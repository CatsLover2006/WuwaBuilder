package ca.litten.discordbot.wuwabuilder;

import static ca.litten.discordbot.wuwabuilder.HakushinInterface.StatPair;

import ca.litten.discordbot.wuwabuilder.wuwa.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CardBuilder {
    private static final Paint mainLightModePaint = new GradientPaint(0, 0, new Color(0xf5f5f5), 1500, 750, new Color(0xcacccf));
    private static final Paint dualLightModePaint = new GradientPaint(0, 0, new Color(0xc02a2c2e, true), 1500, 750, new Color(0xc0000000, true));
    private static final Font font;
    
    private final Paint mainPaint, dualPaint;
    
    private static final HashMap<Stat, Image> cached40statIcons = new HashMap<>();
    private static final HashMap<Stat, Image> cached20statIcons = new HashMap<>();
    private static final HashMap<Stat, Image> cached14statIcons = new HashMap<>();
    private static HashMap<Long, Image> cached35sonataIcons = new HashMap<>();
    
    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("res/Lato-Regular.ttf"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Stat stat : ExtraData.statLogos.keySet()) {
            cached40statIcons.put(stat, ExtraData.statLogos.get(stat)
                    .getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            cached20statIcons.put(stat, ExtraData.statLogos.get(stat)
                    .getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            cached14statIcons.put(stat, ExtraData.statLogos.get(stat)
                    .getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        }
    }
    
    private final boolean isLightMode;
    private final BufferedImage echoMask;
    
    private void initSonataCache() {
        for (long sonata : HakushinInterface.sonataImageCache.keySet()) {
            cached35sonataIcons.put(sonata, HakushinInterface.sonataImageCache.get(sonata)
                    .getScaledInstance(35, 35, Image.SCALE_SMOOTH));
        }
    }
    
    public CardBuilder() {
        isLightMode = true;
        mainPaint = mainLightModePaint;
        dualPaint = dualLightModePaint;
        echoMask = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3d = echoMask.createGraphics();
        g3d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g3d.setPaint(new Color(255, 255, 255));
        g3d.fillRect(0, 20, 120, 80);
        g3d.fillRect(20, 0,80, 120);
        g3d.fillArc(0, 0, 40, 40, 180, -90);
        g3d.fillArc(0, 80, 40, 40, 270, -90);
        g3d.fillArc(80, 80, 40, 40, 0, -90);
        g3d.fillArc(80, 0, 40, 40, 90, -90);
        g3d.dispose();
        initSonataCache();
    }
    
    public CardBuilder(boolean isLightMode) {
        this.isLightMode = isLightMode;
        mainPaint = isLightMode ? mainLightModePaint : mainLightModePaint;
        dualPaint = isLightMode ? dualLightModePaint : dualLightModePaint;
        echoMask = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3d = echoMask.createGraphics();
        g3d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g3d.setPaint(new Color(255, 255, 255));
        g3d.fillRect(0, 20, 120, 80);
        g3d.fillRect(20, 0,80, 120);
        g3d.fillArc(0, 0, 40, 40, 180, -90);
        g3d.fillArc(0, 80, 40, 40, 270, -90);
        g3d.fillArc(80, 80, 40, 40, 0, -90);
        g3d.fillArc(80, 0, 40, 40, 90, -90);
        g3d.dispose();
        initSonataCache();
    }
    
    private static String getStatNumber(StatPair stat) {
        switch (stat.stat) {
            case flatDEF:
            case flatHP:
            case flatATK:
                return String.valueOf((long) stat.value);
            default:
                if (stat.value == (long) stat.value) return String.format("%d%%", (long)stat.value);
                return String.format("%1.1f%%", stat.value);
        }
    }
    
    private void drawWeaponImage(Build build, Graphics2D g2d, int x, int y) {
        Weapon weapon = build.weapon;
        g2d.setPaint(mainPaint);
        g2d.fillArc(x, y, 40, 40, 180, -90);
        g2d.fillArc(x, y + 100, 40, 40, 270, -90);
        g2d.fillArc(x + 240, y + 100, 40, 40, 0, -90);
        g2d.fillArc(x + 240, y, 40, 40, 90, -90);
        g2d.fillRect(x, y + 20, 280, 100);
        g2d.fillRect(x + 20, y, 240, 140);
        BufferedImage virtImage = new BufferedImage(echoMask.getWidth(), echoMask.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int bgColor;
        switch (weapon.getStarCount()) {
            case 5:
                bgColor = 0xAA6C3A;
                break;
            case 4:
                bgColor = 0x8F6EA8;
                break;
            case 3:
                bgColor = 0x5486A7;
                break;
            case 2:
                bgColor = 0x4E7F6F;
                break;
            case 1:
            default: // Idk just go grey
                bgColor = 0x7e7e7e;
                break;
        }
        Graphics2D g3d = virtImage.createGraphics();
        g3d.setPaint(new Color(bgColor));
        g3d.fillRect(0, 0, echoMask.getWidth(), echoMask.getHeight());
        g3d.drawImage(weapon.getImage().getScaledInstance(echoMask.getWidth(), echoMask.getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
        g3d.dispose();
        for (int i = 0; i < echoMask.getHeight(); i++)
            for (int j = 0; j < echoMask.getWidth(); j++) {
                virtImage.setRGB(i, j, virtImage.getRGB(i, j) & ((echoMask.getRGB(i, j) & 0xff000000) | 0x00ffffff));
            }
        g2d.drawImage(virtImage, x + 10, y + 10, null);
        g2d.setPaint(dualPaint);
        g2d.setFont(font.deriveFont(Font.PLAIN, 18));
        String weaponLevelString = build.weaponLevel.toString();
        switch (weaponLevelString.charAt(0)) {
            case 'b':
                weaponLevelString = weaponLevelString + "/40";
                break;
            case 'c':
                weaponLevelString = weaponLevelString + "/50";
                break;
            case 'd':
                weaponLevelString = weaponLevelString + "/60";
                break;
            case 'e':
                weaponLevelString = weaponLevelString + "/70";
                break;
            case 'f':
                weaponLevelString = weaponLevelString + "/80";
                break;
            case 'g':
                weaponLevelString = weaponLevelString + "/90";
                break;
            case 'a':
            default:
                weaponLevelString = weaponLevelString + "/20";
                break;
        }
        weaponLevelString = "Lv. " + weaponLevelString.substring(1);
        g2d.drawString(weaponLevelString, x + 140, y + 68);
        g2d.drawImage(tintImage(cached20statIcons.get(weapon.getMainStat()), dualPaint), x + 140, y + 71, null);
        g2d.drawImage(tintImage(cached20statIcons.get(weapon.getSubStat()), dualPaint), x + 140, y + 91, null);
        g2d.drawString(getStatNumber(new StatPair(weapon.getMainStat(),
                weapon.getMainStatForLevel(build.weaponLevel))), x + 164, y + 88);
        g2d.drawString(getStatNumber(new StatPair(weapon.getSubStat(),
                weapon.getSubStatForLevel(build.weaponLevel))), x + 164, y + 108);
        String[] weaponName = weapon.getName().split(" ");
        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        int idx = 0;
        while (idx != weaponName.length) {
            line1.append(" ").append(weaponName[idx]);
            if (g2d.getFontMetrics().stringWidth(line1.toString()) > 130) break;
            idx++;
        }
        int i;
        line1 = new StringBuilder();
        for (i = 0; i < idx; i++)
            line1.append(" ").append(weaponName[i]);
        if (idx != weaponName.length) {
            while (idx != weaponName.length) {
                line2.append(" ").append(weaponName[idx]);
                if (g2d.getFontMetrics().stringWidth(line2.toString()) > 130) break;
                idx++;
            }
            line2 = new StringBuilder();
            for (; i < idx; i++)
                line2.append(" ").append(weaponName[i]);
            if (idx != weaponName.length) {
                line2.append("...");
                if (g2d.getFontMetrics().stringWidth(line2.toString()) > 130) {
                    line2.delete(line2.lastIndexOf(" "), line2.length() - 1);
                    line2.append("...");
                }
            }
        }
        if (line2.toString().isEmpty()) {
            line2 = line1;
            line1 = new StringBuilder();
        }
        g2d.drawString(line1.toString().trim(), x + 140, y + 28);
        g2d.drawString(line2.toString().trim(), x + 140, y + 48);
    }
    
    private void drawEchoImage(Echo echo, Graphics2D g2d, int x, int y) {
        g2d.setPaint(mainPaint);
        g2d.fillArc(x, y, 40, 40, 180, -90);
        g2d.fillArc(x, y + 100, 40, 40, 270, -90);
        g2d.fillArc(x + 240, y + 100, 40, 40, 0, -90);
        g2d.fillArc(x + 240, y, 40, 40, 90, -90);
        g2d.fillRect(x, y + 20, 280, 100);
        g2d.fillRect(x + 20, y, 240, 140);
        if (echo == null) return;
        BufferedImage virtImage = new BufferedImage(echoMask.getWidth(), echoMask.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3d = virtImage.createGraphics();
        g3d.setPaint(new Color(0x7e7e7e));
        g3d.fillRect(0, 0, echoMask.getWidth(), echoMask.getHeight());
        g3d.drawImage(HakushinInterface.echoImageCache.get(echo.echoID).getScaledInstance(echoMask.getWidth(),
                        echoMask.getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
        g3d.dispose();
        for (int i = 0; i < echoMask.getHeight(); i++)
            for (int j = 0; j < echoMask.getWidth(); j++) {
                virtImage.setRGB(i, j, virtImage.getRGB(i, j) & ((echoMask.getRGB(i, j) & 0xff000000) | 0x00ffffff));
            }
        g2d.drawImage(virtImage, x + 10, y + 10, null);
        g2d.drawImage(cached35sonataIcons.get(echo.sonataID), x + 90, y + 90, null);
        g2d.setPaint(dualPaint);
        g2d.setFont(font.deriveFont(Font.PLAIN, 18));
        g2d.drawImage(tintImage(cached20statIcons.get(echo.secondStat), dualPaint), x + 140, y + 19, null);
        g2d.drawString(getStatNumber(new StatPair(echo.secondStat, echo.secondStatMagnitude)), x + 164, y + 35);
        g2d.setFont(font.deriveFont(Font.PLAIN, 12));
        Stat[] substats = echo.subStats.keySet().toArray(new Stat[0]);
        g2d.drawImage(tintImage(cached14statIcons.get(echo.mainStat), dualPaint), x + 215, y + 20, null);
        g2d.drawString(getStatNumber(new StatPair(echo.mainStat, echo.mainStatMagnitude)), x + 235, y + 32);
        for (int i = 0; i < substats.length; i++) {
            g2d.drawImage(tintImage(cached14statIcons.get(substats[i]), dualPaint), x + 140, y + 41 + 16 * i, null);
            g2d.drawString(getStatNumber(new StatPair(substats[i], echo.subStats.get(substats[i]))), x + 160, y + 52 + 16 * i);
        }
    }
    
    private static void drawCircledImage(Graphics2D g2d, int x, int y, int r, BufferedImage image, Paint front, Paint back) {
        drawCircledImage(g2d, x, y, r, image, 1, front, back);
    }
    
    private static void drawCircledImage(Graphics2D g2d, int x, int y, int r, BufferedImage image, double relativeImageSize, Paint front, Paint back) {
        g2d.setPaint(back);
        g2d.fillArc(x, y, r, r, 0, 360);
        g2d.drawImage(tintImage(image.getScaledInstance((int) (r * relativeImageSize),
                        (int) (r * relativeImageSize), Image.SCALE_SMOOTH), front),
                x + (int) (r * (1 - relativeImageSize) / 2),
                y + (int) (r * (1 - relativeImageSize) / 2), null);
    }
    
    private static BufferedImage tintImage(Image image, Paint color){
        BufferedImage imageTint = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3d = imageTint.createGraphics();
        g3d.drawImage(image, 0, 0, null);
        g3d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g3d.setPaint(color);
        g3d.fillRect(0, 0, imageTint.getWidth(), imageTint.getHeight());
        g3d.dispose();
        return imageTint;
    }
    
    public BufferedImage createCard(Build build) {
        StatPage statPage = StatPage.calculateStats(build);
        BufferedImage output = new BufferedImage(1500, 750, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setPaint(mainPaint);
        g2d.fillRect(0, 0, 1500, 750);
        BufferedImage cachedImage = build.character.getImage();
        g2d.drawImage(cachedImage.getScaledInstance(800,-1, Image.SCALE_SMOOTH), 0, -100, null);
        g2d.setPaint(dualPaint);
        g2d.fillRect(800, 0, 700, 750);
        int characterNameHeight = 90;
        int characterNameWidth = Integer.MAX_VALUE;
        while (characterNameWidth > 500) {
            characterNameHeight -= 10;
            g2d.setFont(font.deriveFont(Font.PLAIN, (int)(characterNameHeight * 0.9)));
            characterNameWidth = g2d.getFontMetrics().stringWidth(build.character.getName());
        }
        int characterPositionX = 400 - characterNameWidth/2;
        int characterNameHeightDiv10 = characterNameHeight/10;
        g2d.fillRect(characterPositionX, 0, characterNameWidth, characterNameHeight);
        g2d.fillRect(characterPositionX + characterNameWidth, 0,
                characterNameHeightDiv10, characterNameHeight - characterNameHeightDiv10);
        g2d.fillRect(characterPositionX - characterNameHeightDiv10, 0,
                characterNameHeightDiv10, characterNameHeight - characterNameHeightDiv10);
        g2d.fillArc(characterPositionX + characterNameWidth - characterNameHeightDiv10,
                characterNameHeight - characterNameHeightDiv10 * 2, characterNameHeightDiv10 * 2,
                characterNameHeightDiv10 * 2, 0, -90);
        g2d.fillArc(characterPositionX - characterNameHeightDiv10,
                characterNameHeight - characterNameHeightDiv10 * 2, characterNameHeightDiv10 * 2,
                characterNameHeightDiv10 * 2, 180, 90);
        g2d.setPaint(mainPaint);
        g2d.fillRect(875, 0, 10, 750);
        g2d.drawString(build.character.getName(), characterPositionX,
                characterNameHeight - characterNameHeightDiv10 * 2);
        int characterLevelHeight = 35;
        int characterLevelHeightDiv10 = characterLevelHeight/10;
        g2d.setFont(font.deriveFont(Font.PLAIN, characterLevelHeight - characterLevelHeightDiv10));
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
        characterPositionX = 400 - characterLevelWidth/2;
        g2d.setPaint(dualPaint);
        g2d.fillRect(characterPositionX, characterNameHeight, characterLevelWidth, characterLevelHeight);
        g2d.fillRect(characterPositionX + characterLevelWidth, characterNameHeight,
                characterLevelHeightDiv10, characterLevelHeight - characterLevelHeightDiv10);
        g2d.fillRect(characterPositionX - characterLevelHeightDiv10, characterNameHeight,
                characterLevelHeightDiv10, characterLevelHeight - characterLevelHeightDiv10);
        g2d.fillArc(characterPositionX + characterLevelWidth - characterLevelHeightDiv10,
                characterNameHeight + characterLevelHeight - characterLevelHeightDiv10 * 2, characterLevelHeightDiv10 * 2,
                characterLevelHeightDiv10 * 2, 0, -90);
        g2d.fillArc(characterPositionX - characterLevelHeightDiv10,
                characterNameHeight + characterLevelHeight - characterLevelHeightDiv10 * 2, characterLevelHeightDiv10 * 2,
                characterLevelHeightDiv10 * 2, 180, 90);
        g2d.setPaint(mainPaint);
        g2d.drawString(characterLevelText, characterPositionX, characterNameHeight + characterLevelHeight - characterLevelHeightDiv10 * 2);
        for (int i = 0; i < 6; i++) {
            drawCircledImage(g2d, 812, 65 * i + 12, 51, build.character.getChain(i), 0.9,
                    new Color(0x80151617, !(build.chainLength > i)),
                    new Color(0x40ffffff, !(build.chainLength > i)));
        }
        g2d.setPaint(mainPaint);
        g2d.setFont(font.deriveFont(Font.PLAIN, 38));
        ArrayList<StatPair> pickedStats = new ArrayList<>();
        pickedStats.add(new StatPair(Stat.aeroBonus, statPage.aeroBonus));
        pickedStats.add(new StatPair(Stat.electroBonus, statPage.electroBonus));
        pickedStats.add(new StatPair(Stat.fusionBonus, statPage.fusionBonus));
        pickedStats.add(new StatPair(Stat.glacioBonus, statPage.glacioBonus));
        pickedStats.add(new StatPair(Stat.havocBonus, statPage.havocBonus));
        pickedStats.add(new StatPair(Stat.spectroBonus, statPage.spectroBonus));
        pickedStats.add(new StatPair(Stat.basicBonus, statPage.basicBonus));
        pickedStats.add(new StatPair(Stat.heavyBonus, statPage.heavyBonus));
        pickedStats.add(new StatPair(Stat.skillBonus, statPage.skillBonus));
        pickedStats.add(new StatPair(Stat.ultBonus, statPage.ultBonus));
        pickedStats.add(new StatPair(Stat.healingBonus, statPage.healingBonus));
        for (int i = pickedStats.size() - 1; i >= 0; i--)
            if (pickedStats.get(i).value == 0) pickedStats.remove(i);
        pickedStats.sort((a, b) -> (int)(1000 * (b.value - a.value)));
        g2d.drawImage(tintImage(cached40statIcons.get(Stat.flatHP), mainPaint), 1145, 10, null);
        g2d.drawImage(tintImage(cached40statIcons.get(Stat.flatATK), mainPaint), 1145, 60, null);
        g2d.drawImage(tintImage(cached40statIcons.get(Stat.flatDEF), mainPaint), 1145, 110, null);
        String statText = getStatNumber(new StatPair(Stat.flatHP, statPage.HP));
        g2d.drawString(statText, 1135 - g2d.getFontMetrics().stringWidth(statText), 45);
        statText = getStatNumber(new StatPair(Stat.flatATK, statPage.ATK));
        g2d.drawString(statText, 1135 - g2d.getFontMetrics().stringWidth(statText), 95);
        statText = getStatNumber(new StatPair(Stat.flatDEF, statPage.DEF));
        g2d.drawString(statText, 1135 - g2d.getFontMetrics().stringWidth(statText), 145);
        if (pickedStats.size() < 3) {
            g2d.drawImage(tintImage(cached40statIcons.get(Stat.energyRegen), mainPaint), 1145, 160, null);
            statText = getStatNumber(new StatPair(Stat.energyRegen, statPage.energyRegen));
            g2d.drawString(statText, 1135 - g2d.getFontMetrics().stringWidth(statText), 195);
            g2d.drawImage(tintImage(cached40statIcons.get(Stat.critRate), mainPaint), 1200, 10, null);
            g2d.drawString(getStatNumber(new StatPair(Stat.critRate, statPage.critRate)), 1245, 45);
            pickedStats.add(0, new StatPair(Stat.critDMG, statPage.critDMG));
        } else {
            g2d.drawImage(tintImage(cached40statIcons.get(Stat.critRate), mainPaint), 1145, 160, null);
            g2d.drawImage(tintImage(cached40statIcons.get(Stat.critDMG), mainPaint), 1145, 210, null);
            g2d.drawImage(tintImage(cached40statIcons.get(Stat.energyRegen), mainPaint), 1200, 10, null);
            statText = getStatNumber(new StatPair(Stat.critRate, statPage.critRate));
            g2d.drawString(statText, 1135 - g2d.getFontMetrics().stringWidth(statText), 195);
            statText = getStatNumber(new StatPair(Stat.critDMG, statPage.critDMG));
            g2d.drawString(statText, 1135 - g2d.getFontMetrics().stringWidth(statText), 245);
            g2d.drawString(getStatNumber(new StatPair(Stat.energyRegen, statPage.energyRegen)), 1245, 45);
        }
        for (int i = 0; i < 4; i++)
            if (pickedStats.size() > i) {
                g2d.drawImage(tintImage(cached40statIcons.get(pickedStats.get(i).stat), mainPaint),
                        1200, 60 + 50 * i, null);
                g2d.drawString(getStatNumber(pickedStats.get(i)), 1250, 95 + 50 * i);
            }
        // Availiable space for echoes: 280 x 150
        drawWeaponImage(build, g2d, 903, 270);
        drawEchoImage(build.echoes[0], g2d, 1202, 270);
        drawEchoImage(build.echoes[1], g2d, 903, 430);
        drawEchoImage(build.echoes[2], g2d, 1202, 430);
        drawEchoImage(build.echoes[3], g2d, 903, 590);
        drawEchoImage(build.echoes[4], g2d, 1202, 590);
        g2d.dispose();
        return output;
    }
}
