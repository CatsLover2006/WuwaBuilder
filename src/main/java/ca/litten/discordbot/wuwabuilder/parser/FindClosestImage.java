package ca.litten.discordbot.wuwabuilder.parser;

import ca.litten.discordbot.wuwabuilder.WuwaDatabase;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class FindClosestImage {
    public static long findClosestSonata(BufferedImage sonataImage) {
        long biggestDif = Long.MAX_VALUE;
        long closestSonata = -1;
        Set<Long> toCheck = WuwaDatabase.sonataImageCache.keySet();
        int dim = Math.max(sonataImage.getHeight(), sonataImage.getWidth());
        int heightMod = (dim - sonataImage.getHeight()) / 2;
        int widthMod = (dim - sonataImage.getWidth()) / 2;
        long difSum;
        BufferedImage bufferedCheckImage = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        for (long sonata : toCheck) {
            difSum = 0;
            Graphics2D g2d = bufferedCheckImage.createGraphics();
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, dim, dim);
            g2d.drawImage(WuwaDatabase.sonataImageCache.get(sonata)
                    .getScaledInstance(dim, dim, Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();
            for (int x = 0; x < sonataImage.getWidth(); x++)
                for (int y = 0; y < sonataImage.getHeight(); y++) {
                    difSum += getColorDif(sonataImage.getRGB(x, y), bufferedCheckImage.getRGB(x + widthMod, y + heightMod));
                }
            if (biggestDif > difSum) {
                biggestDif = difSum;
                closestSonata = sonata;
            }
        }
        return closestSonata;
    }
    
    private static int getColorDif(int rgb1, int rgb2) {
        int alpha1 = (rgb1 >> 24) & 0x0ff;
        int red1 = (rgb1 >> 16) & 0x0ff;
        int green1 = (rgb1 >> 8) & 0x0ff;
        int blue1 = rgb1 & 0x0ff;
        int alpha2 = (rgb2 >> 24) & 0x0ff;
        int red2 = (rgb2 >> 16) & 0x0ff;
        int green2 = (rgb2 >> 8) & 0x0ff;
        int blue2 = rgb2 & 0x0ff;
        return (Math.abs(red1 - red2) + Math.abs(green1 - green2) + Math.abs(blue1 - blue2)) * Math.min(alpha1, alpha2);
    }
    
    private static final Color bgColor = new Color(20, 15, 22);
    
    public static long findClosestEcho(BufferedImage echoImage) {
        return findClosestEcho(echoImage, -1);
    }
    
    public static long findClosestEcho(BufferedImage echoImage, long sonata) {
        long biggestDif = Long.MAX_VALUE;
        long closestID = -1;
        Set<Long> toCheck;
        if (sonata == -1) toCheck = WuwaDatabase.echoImageCache.keySet();
        else toCheck = new HashSet<>(WuwaDatabase.sonataEchoCache.get(sonata));
        int dim = Math.max(echoImage.getHeight(), echoImage.getWidth());
        int heightMod = (dim - echoImage.getHeight()) / 2;
        int widthMod = (dim - echoImage.getWidth()) / 2;
        long difSum;
        BufferedImage bufferedCheckImage = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        for (long id : toCheck) {
            difSum = 0;
            Graphics2D g2d = bufferedCheckImage.createGraphics();
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, dim, dim);
            g2d.drawImage(WuwaDatabase.echoImageCache.get(id)
                    .getScaledInstance(dim, dim, Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();
            for (int x = 0; x < echoImage.getWidth(); x++)
                for (int y = 0; y < echoImage.getHeight(); y++) {
                    difSum += getColorDif(echoImage.getRGB(x, y), bufferedCheckImage.getRGB(x + widthMod, y + heightMod));
                }
            if (biggestDif > difSum) {
                biggestDif = difSum;
                closestID = id;
            }
        }
        if (biggestDif > 1000000000 && sonata != -1) return findClosestEcho(echoImage, -1);
        return closestID;
    }
}
