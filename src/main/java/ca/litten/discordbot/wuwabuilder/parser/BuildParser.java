package ca.litten.discordbot.wuwabuilder.parser;


import static ca.litten.discordbot.wuwabuilder.WuwaDatabaseLoader.StatPair;

import ca.litten.discordbot.wuwabuilder.wuwa.*;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import org.bytedeco.tesseract.*;

public class BuildParser {
    private static int[] sequenceX = {190, 264, 345, 425, 505, 585};
    private static int[] echoX = {25, 398, 772, 1147, 1520};
    private static int[] skillX = {1172, 1053, 820, 1256, 912};
    private static int[] skillY = {588, 185, 341, 341, 588};
    
    private static int[] roverColors = {0x7b6b4d, 0x822f6a, 0x678682};
    
    private static String ocrExec(TessBaseAPI ocr, BufferedImage subsegment) {
        byte[] OCRbytes = new byte[subsegment.getHeight() * subsegment.getWidth() * 3];
        for (int y = 0; y < subsegment.getHeight(); y++)
            for (int x = 0; x < subsegment.getWidth(); x++) {
                int rgb = subsegment.getRGB(x, y);
                OCRbytes[(x + y * subsegment.getWidth()) * 3] = (byte) ((rgb >> 16) & 0x0ff);
                OCRbytes[(x + y * subsegment.getWidth()) * 3 + 1] = (byte) ((rgb >> 8) & 0x0ff);
                OCRbytes[(x + y * subsegment.getWidth()) * 3 + 2] = (byte) (rgb & 0x0ff);
            }
        ocr.SetImage(OCRbytes, subsegment.getWidth(), subsegment.getHeight(), 3, 3 * subsegment.getWidth());
        return ocr.GetUTF8Text().getString(StandardCharsets.UTF_8).trim().replace("\n", " ");
    }
    
    private static String ocrExec_hp(TessBaseAPI ocr, BufferedImage subsegment) {
        byte[] OCRbytes = new byte[subsegment.getHeight() * subsegment.getWidth() * 3];
        for (int y = 0; y < subsegment.getHeight(); y++)
            for (int x = 0; x < subsegment.getWidth(); x++) {
                int rgb = subsegment.getRGB(x, y);
                OCRbytes[(x + y * subsegment.getWidth()) * 3] = (byte) ((rgb >> 16) & 0x080);
                OCRbytes[(x + y * subsegment.getWidth()) * 3 + 1] = (byte) ((rgb >> 8) & 0x080);
                OCRbytes[(x + y * subsegment.getWidth()) * 3 + 2] = (byte) (rgb & 0x080);
            }
        ocr.SetImage(OCRbytes, subsegment.getWidth(), subsegment.getHeight(), 3, 3 * subsegment.getWidth());
        return ocr.GetUTF8Text().getString(StandardCharsets.UTF_8).trim().replace("\n", " ");
    }
    
    private static Level levelLookup(int level) {
        if (level > 80) return levelLookup(level, 6);
        if (level > 70) return levelLookup(level, 5);
        if (level > 60) return levelLookup(level, 4);
        if (level > 50) return levelLookup(level, 3);
        if (level > 40) return levelLookup(level, 2);
        if (level > 20) return levelLookup(level, 1);
        return levelLookup(level, 0);
    }
    
    private static Level levelLookup(int level, int ascension) {
        return Level.valueOf("abcdefghi".charAt(ascension) + String.valueOf(level));
    }
    
    public static StatPair readStat(String name, String value) {
        value = value.replaceAll("[^0-9\\.%]", "");
        float stat = Float.parseFloat("0" + value.replace("%", ""));
        switch (name) {
            case "ATK":
                if (value.endsWith("%")) return new StatPair(Stat.percentATK, stat);
                return new StatPair(Stat.flatATK, stat);
            case "DEF":
                if (value.endsWith("%")) return new StatPair(Stat.percentDEF, stat);
                return new StatPair(Stat.flatDEF, stat);
            case "HP":
                if (value.endsWith("%")) return new StatPair(Stat.percentHP, stat);
                return new StatPair(Stat.flatHP, stat);
            case "Crit. DMG":
                return new StatPair(Stat.critDMG, stat);
            case "Crit. Rate":
                return new StatPair(Stat.critRate, stat);
            case "Energy Regen":
                return new StatPair(Stat.energyRegen, stat);
            case "Havoc DMG Bonus":
                return new StatPair(Stat.havocBonus, stat);
            case "Glacio DMG Bonus":
                return new StatPair(Stat.glacioBonus, stat);
            case "Fusion DMG Bonus":
                return new StatPair(Stat.fusionBonus, stat);
            case "Electro DMG Bonus":
                return new StatPair(Stat.electroBonus, stat);
            case "Aero DMG Bonus":
                return new StatPair(Stat.aeroBonus, stat);
            case "Spectro DMG Bonus":
                return new StatPair(Stat.spectroBonus, stat);
            case "Basic Attack DMG Bonus":
                return new StatPair(Stat.basicBonus, stat);
            case "Heavy Attack DMG Bonus":
                return new StatPair(Stat.heavyBonus, stat);
            case "Resonance Skill DMG Bonus":
                return new StatPair(Stat.skillBonus, stat);
            case "Resonance Liberation DMG Bonus":
                return new StatPair(Stat.ultBonus, stat);
            case "Healing Bonus":
                return new StatPair(Stat.healingBonus, stat);
        }
        return null;
    }
    
    private static String nameConvert(String value, BufferedImage image) {
        // Hardcoded defuckage
        if (value.startsWith("l")) // Iuno is read as luno
            value = "I" + value.substring(1);
        // Rover is pain
        int x, y, rgb;
        if (value.equals("Rover")) {
            int totalRed = 0, totalGreen = 0, totalBlue = 0;
            for (x = 0; x < 42; x++)
                for (y = 0; y < 42; y++) {
                    rgb = image.getRGB(x + 20, y + 29);
                    totalRed += (rgb >> 16) & 0x0ff;
                    totalGreen += (rgb >> 8) & 0x0ff;
                    totalBlue += rgb & 0x0ff;
                }
            totalRed /= 42 * 42;
            totalGreen /= 42 * 42;
            totalBlue /= 42 * 42;
            int closestDif = 255 * 3;
            int closestIndex = 0;
            int colorDif;
            for (int i = 0; i < roverColors.length; i++) {
                colorDif = Math.abs(((roverColors[i] >> 16) & 0x0ff) - totalRed)
                        + Math.abs(((roverColors[i] >> 8) & 0x0ff) - totalGreen)
                        + Math.abs((roverColors[i] & 0x0ff) - totalBlue);
                if (closestDif > colorDif) {
                    closestIndex = i;
                    closestDif = colorDif;
                }
            }
            switch (closestIndex) {
                case 2:
                    value = "Rover: Aero";
                    break;
                case 1:
                    value = "Rover: Havoc";
                    break;
                case 0:
                default: // IDK WHAT'S GOING ON
                    value = "Rover: Spectro";
            }
        }
        if (value.contains("Rover:")) {
            // 384x312
            if (((image.getRGB(384, 312) >> 16) & 0x0ff) > 0xC0)
                value = "F" + value;
            else value = "M" + value;
        }
        return value;
    }
    
    public static Build parseBuild(BufferedImage image) {
        Build build = new Build();
        build.chainLength = 0;
        for (int i = 0; i < 6; i++) {
            int resonanceRGB = image.getRGB(sequenceX[i], 576);
            if (((resonanceRGB >> 16) & 0x0ff) > 100) build.chainLength = i + 1;
        }
        TessBaseAPI ocr_name = new TessBaseAPI();
        TessBaseAPI ocr_numbers = new TessBaseAPI();
        TessBaseAPI ocr_digits = new TessBaseAPI();
        TessBaseAPI ocr_weap = new TessBaseAPI();
        if (ocr_name.Init(null, "eng", 1) != 0
                || ocr_numbers.Init(null, "eng", 1) != 0
                || ocr_weap.Init(null, "eng", 1) != 0
                || ocr_digits.Init(null, "eng", 1) != 0) {
            throw new RuntimeException("Failed to start tesseract");
        }
        ocr_name.SetVariable("tessedit_char_whitelist", "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm .:");
        ocr_weap.SetVariable("tessedit_char_whitelist", "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm" +
                " .-':1234890#");
        ocr_numbers.SetVariable("tessedit_char_whitelist", "1234567890/.%");
        ocr_digits.SetVariable("tessedit_char_whitelist", "1234567890.");
        BufferedImage charaLevel = image.getSubimage(68, 41, 686, 23);
        // 70 wide
        boolean columnGood;
        int x, y, r, b, rgb;
        for (x = 0; x < charaLevel.getWidth(); x++) {
            columnGood = true;
            for (y = 0; y < charaLevel.getHeight(); y++) {
                rgb = charaLevel.getRGB(x, y);
                r = (rgb >> 16) & 0x0ff;
                b = rgb & 0x0ff;
                if (r < 170) columnGood = false;
                if (b > 150) columnGood = false;
                if (!columnGood) break;
            }
            if (columnGood) break;
        }
        BufferedImage subimage;
        try {
            subimage = charaLevel.getSubimage(x, 0, 55, charaLevel.getHeight());
        } catch (Exception e) {
            subimage = charaLevel; // Backup
        }
        build.characterLevel = levelLookup(Integer.parseInt(
                ocrExec(ocr_digits, subimage).replace(".", "")));
        if (build.characterLevel == null)
            build.characterLevel = levelLookup(Integer.parseInt(
                    ocrExec_hp(ocr_digits, subimage).replace(".", "")));
        subimage = image.getSubimage(68, 23, x - 4, 65);
        String value = ocrExec(ocr_name, subimage);
        value = nameConvert(value, image);
        build.character = Character.getCharacterByName(value);
        if (build.character == null) {
            value = ocrExec_hp(ocr_name, subimage);
            value = nameConvert(value, image);
            build.character = Character.getCharacterByName(value);
        }
        System.out.println(build.character + ": " + build.characterLevel);
        subimage = image.getSubimage(1602, 455, 275, 24);
        build.weapon = Weapon.getWeaponByName(ocrExec(ocr_weap, subimage));
        if (build.weapon == null)
            build.weapon = Weapon.getWeaponByName(ocrExec_hp(ocr_weap, subimage));
        // IDK if this even is on the thing, R1 for 5 stars, R5 otherwise
        build.weaponRank = build.weapon.getStarCount() == 5 ? 0 : 4;
        // Get weapon ascension
        int ascension;
        for (ascension = 0; ascension <= 6; ascension++) {
            if ((image.getRGB(1618 + 25 * ascension, 599) & 0x00ff0000) < 0x00800000)
                break;
        }
        subimage = image.getSubimage(1640, 506, 70, 32);
        value = ocrExec(ocr_digits, subimage);
        build.weaponLevel = levelLookup(Integer.parseInt(value.substring(value.lastIndexOf('.') + 1)), ascension);
        if (build.weaponLevel == null) {
            value = ocrExec_hp(ocr_digits, subimage);
            build.weaponLevel = levelLookup(Integer.parseInt(value.substring(value.lastIndexOf('.') + 1)), ascension);
        }
        System.out.println(build.weapon + ": " + build.weaponLevel);
        // Echoes
        String stat;
        for (int i = 0; i < 5; i++) {
            long sonata = FindClosestImage.findClosestSonata(image.getSubimage(echoX[i] + 243, 663, 48, 48));
            long echoID = FindClosestImage.findClosestEcho(image.getSubimage(echoX[i], 654, 190, 180), sonata);
            BufferedImage OCRimage = image.getSubimage(echoX[i] + 17, 726, 338, 335);
            stat = ocrExec(ocr_name, OCRimage.getSubimage(22, 119, 228, 25));
            value = ocrExec(ocr_numbers, OCRimage.getSubimage(250, 119, 88, 25));
            StatPair mainStat = readStat(stat, value);
            if (mainStat == null) {
                stat = ocrExec_hp(ocr_name, OCRimage.getSubimage(22, 119, 228, 25));
                value = ocrExec_hp(ocr_numbers, OCRimage.getSubimage(250, 119, 88, 25));
                mainStat = readStat(stat, value);
            }
            stat = ocrExec(ocr_name, OCRimage.getSubimage(179, 0, 159, 19));
            value = ocrExec(ocr_numbers, OCRimage.getSubimage(179, 19, 159, 43));
            StatPair stat2 = readStat(stat, value);
            if (stat2 == null) {
                stat = ocrExec_hp(ocr_name, OCRimage.getSubimage(179, 0, 159, 19));
                value = ocrExec_hp(ocr_numbers, OCRimage.getSubimage(179, 19, 159, 43));
                stat2 = readStat(stat, value);
            }
            HashMap<Stat, Float> subStats = new HashMap<>();
            StatPair subStat;
            for (int s = 0; s < 5; s++) {
                stat = ocrExec(ocr_name, OCRimage.getSubimage(20, 159 + 34 * s, 230, 34));
                value = ocrExec(ocr_numbers, OCRimage.getSubimage(250, 157 + 34 * s, 88, 38));
                subStat = readStat(stat, value);
                if (subStat == null || subStat.value < 5) {
                    stat = ocrExec_hp(ocr_name, OCRimage.getSubimage(20, 159 + 34 * s, 230, 34));
                    value = ocrExec_hp(ocr_numbers, OCRimage.getSubimage(250, 157 + 34 * s, 88, 38));
                    subStat = readStat(stat, value);
                }
                if (subStat != null) {
                    subStats.put(subStat.stat, subStat.value);
                }
            }
            build.echoes[i] = new Echo(echoID, sonata, mainStat.stat, mainStat.value,
                    stat2.stat, stat2.value % 100, subStats);
        }
        // Skills
        for (int i = 0; i < 5; i++) {
            BufferedImage temp = image.getSubimage(skillX[i], skillY[i], 113, 25);
            try {
                value = ocrExec(ocr_numbers, temp);
                int dot = value.indexOf('.');
                int slash = value.lastIndexOf('/');
                if (dot == -1) dot = 0;
                value = value.substring(dot < slash ? dot : 0, slash).replace(".", "");
                build.skillLevels[i] = Integer.parseInt(value);
            } catch (Exception e) {
                value = ocrExec_hp(ocr_numbers, temp);
                int dot = value.indexOf('.');
                int slash = value.lastIndexOf('/');
                if (dot == -1) dot = 0;
                value = value.substring(dot < slash ? dot : 0, slash).replace(".", "");
                build.skillLevels[i] = Integer.parseInt(value);
            }
        }
        // Minors (for stat page math)
        build.asensionPassive = 0;
        switch (build.characterLevel.toString().charAt(0)) {
            case 'g': // A6
            case 'f': // A5
                build.minorSkills[1] = true;
                build.minorSkills[7] = true;
            case 'e': // A4
                build.minorSkills[3] = true;
                build.minorSkills[5] = true;
                build.asensionPassive = 1;
            case 'd': // A3
                build.minorSkills[0] = true;
                build.minorSkills[6] = true;
            case 'c': // A2
                build.minorSkills[2] = true;
                build.minorSkills[4] = true;
                build.asensionPassive++;
        }
        ocr_name.End();
        ocr_numbers.End();
        ocr_weap.End();
        ocr_digits.End();
        return build;
    }
}
