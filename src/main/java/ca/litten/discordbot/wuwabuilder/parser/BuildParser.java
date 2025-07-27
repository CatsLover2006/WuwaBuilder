package ca.litten.discordbot.wuwabuilder.parser;


import static ca.litten.discordbot.wuwabuilder.HakushinInterface.StatPair;
import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.wuwa.*;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import org.bytedeco.tesseract.*;

public class BuildParser {
    private static int[] sequenceX = {190, 264, 345, 425, 505, 585};
    private static int[] echoX = {25, 398, 772, 1147, 1520};
    
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
    
    private static StatPair readStat(String name, String value) {
        value = value.replaceAll("[^0-9\\.%]", "");
        switch (name) {
            case "ATK":
                if (value.endsWith("%")) return new StatPair(Stat.percentATK,
                        Float.parseFloat(value.replace("%","")));
                return new StatPair(Stat.flatATK, Float.parseFloat(value.replace("%","")));
            case "DEF":
                if (value.endsWith("%")) return new StatPair(Stat.percentDEF,
                        Float.parseFloat(value.replace("%","")));
                return new StatPair(Stat.flatDEF, Float.parseFloat(value.replace("%","")));
            case "HP":
                if (value.endsWith("%")) return new StatPair(Stat.percentHP,
                        Float.parseFloat(value.replace("%","")));
                return new StatPair(Stat.flatHP, Float.parseFloat(value.replace("%","")));
            case "Crit. DMG":
                return new StatPair(Stat.critDMG, Float.parseFloat(value.replace("%","")));
            case "Crit. Rate":
                return new StatPair(Stat.critRate, Float.parseFloat(value.replace("%","")));
            case "Energy Regen":
                return new StatPair(Stat.energyRegen, Float.parseFloat(value.replace("%","")));
            case "Havoc DMG Bonus":
                return new StatPair(Stat.havocBonus, Float.parseFloat(value.replace("%","")));
            case "Glacio DMG Bonus":
                return new StatPair(Stat.glacioBonus, Float.parseFloat(value.replace("%","")));
            case "Fusion DMG Bonus":
                return new StatPair(Stat.fusionBonus, Float.parseFloat(value.replace("%","")));
            case "Electro DMG Bonus":
                return new StatPair(Stat.electroBonus, Float.parseFloat(value.replace("%","")));
            case "Aero DMG Bonus":
                return new StatPair(Stat.aeroBonus, Float.parseFloat(value.replace("%","")));
            case "Spectro DMG Bonus":
                return new StatPair(Stat.spectroBonus, Float.parseFloat(value.replace("%","")));
            case "Basic Attack DMG Bonus":
                return new StatPair(Stat.naBonus, Float.parseFloat(value.replace("%","")));
            case "Heavy Attack DMG Bonus":
                return new StatPair(Stat.heavyBonus, Float.parseFloat(value.replace("%","")));
            case "Resonance Skill DMG Bonus":
                return new StatPair(Stat.skillBonus, Float.parseFloat(value.replace("%","")));
            case "Resonance Liberation DMG Bonus":
                return new StatPair(Stat.ultBonus, Float.parseFloat(value.replace("%","")));
            case "Healing Bonus":
                return new StatPair(Stat.healingBonus, Float.parseFloat(value.replace("%","")));
        }
        return null;
    }
    
    public static Build parseBuild(BufferedImage image) {
        Build build = new Build();
        build.chainLength = 0;
        for (int i = 0; i < 6; i++) {
            int resonanceRGB = image.getRGB(sequenceX[i], 576);
            if (((resonanceRGB >> 16) & 0x0ff) > 100) build.chainLength = i + 1;
        }
        TessBaseAPI ocr_name = new TessBaseAPI();
        TessBaseAPI ocr_digits = new TessBaseAPI();
        if (ocr_name.Init(null, "eng", 1) != 0) {
            throw new RuntimeException("Failed to start tesseract");
        }
        if (ocr_digits.Init(null, "eng", 1) != 0) {
            throw new RuntimeException("Failed to start tesseract");
        }
        ocr_name.SetVariable("tessedit_char_whitelist", "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm .");
        ocr_digits.SetVariable("tessedit_char_whitelist", "1234567890.%");
        build.weaponRank = 1; // IDK if this even is on the thing
        build.character = Character.getCharacterByName(
                ocrExec(ocr_name, image.getSubimage(68, 23, 686, 65))
                        .replace("LV", ""));
        String stat, value;
        for (int i = 0; i < 5; i++) {
            long sonata = FindClosestImage.findClosestSonata(image.getSubimage(echoX[i] + 243, 663, 48, 48));
            long echoID = FindClosestImage.findClosestEcho(image.getSubimage(echoX[i], 654, 190, 180), sonata);
            BufferedImage OCRimage = image.getSubimage(echoX[i] + 17, 726, 338, 335);
            stat = ocrExec(ocr_name, OCRimage.getSubimage(28, 119, 222, 25));
            value = ocrExec(ocr_digits, OCRimage.getSubimage(250, 119, 88, 25));
            StatPair mainStat = readStat(stat, value);
            stat = ocrExec(ocr_name, OCRimage.getSubimage(179, 0, 159, 19));
            value = ocrExec(ocr_digits, OCRimage.getSubimage(179, 19, 159, 43));
            StatPair stat2 = readStat(stat, value);
            HashMap<Stat, Float> subStats = new HashMap<>();
            StatPair subStat;
            for (int s = 0; s < 5; s++) {
                stat = ocrExec(ocr_name, OCRimage.getSubimage(20, 159 + 34 * s, 230, 34));
                value = ocrExec(ocr_digits, OCRimage.getSubimage(250, 157 + 34 * s, 88, 38));
                subStat = readStat(stat, value);
                if (subStat != null) {
                    subStats.put(subStat.stat, subStat.value);
                }
            }
            build.echoes[i] = new Echo(echoID, sonata, mainStat.stat, mainStat.value,
                    stat2.stat, stat2.value, subStats);
        }
        ocr_name.End();
        return build;
    }
}
