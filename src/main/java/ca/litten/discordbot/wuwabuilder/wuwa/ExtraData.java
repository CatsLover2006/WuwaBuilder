package ca.litten.discordbot.wuwabuilder.wuwa;

import javax.imageio.ImageIO;

import static ca.litten.discordbot.wuwabuilder.WuwaDatabase.StatPair;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ExtraData {
    public static class Sonata {
        long sonataID;
        int count;
        
        public Sonata(long sonataID, int count) {
            this.sonataID = sonataID;
            this.count = count;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Sonata)) return false;
            Sonata sonata = (Sonata) obj;
            return sonata.sonataID == this.sonataID && sonata.count == this.count;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(sonataID, count);
        }
    }
    
    // Unconditional buffs
    public static final HashMap<Sonata, StatPair[]> sonataBuffs = new HashMap<>();
    public static final HashMap<Stat, BufferedImage> statLogos = new HashMap<>();
    public static final BufferedImage githubLogo;
    
    private static final StatPair[] noBuffs = new StatPair[0];
    
    public static StatPair[] getSonataBuffs(Sonata sonata) {
        ArrayList<StatPair> found = new ArrayList<>();
        while (sonata.count > 0) {
            found.addAll(Arrays.asList(sonataBuffs.getOrDefault(sonata, noBuffs)));
            sonata.count--;
        }
        return found.toArray(new StatPair[0]);
    }
    
    static {
        try {
            // Github Logo
            githubLogo = ImageIO.read(new File("res/github.png"));
            // Stat Logos
            BufferedImage atk = ImageIO.read(new File("res/attack.png"));
            BufferedImage def = ImageIO.read(new File("res/defense.png"));
            BufferedImage hp = ImageIO.read(new File("res/hp.png"));
            statLogos.put(Stat.flatATK, atk);
            statLogos.put(Stat.percentATK, atk);
            statLogos.put(Stat.flatDEF, def);
            statLogos.put(Stat.percentDEF, def);
            statLogos.put(Stat.flatHP, hp);
            statLogos.put(Stat.percentHP, hp);
            statLogos.put(Stat.critRate, ImageIO.read(new File("res/critRate.png")));
            statLogos.put(Stat.critDMG, ImageIO.read(new File("res/critDMG.png")));
            statLogos.put(Stat.energyRegen, ImageIO.read(new File("res/energyRegen.png")));
            statLogos.put(Stat.basicBonus, ImageIO.read(new File("res/basicBonus.png")));
            statLogos.put(Stat.heavyBonus, ImageIO.read(new File("res/heavyBonus.png")));
            statLogos.put(Stat.skillBonus, ImageIO.read(new File("res/skillBonus.png")));
            statLogos.put(Stat.ultBonus, ImageIO.read(new File("res/ultBonus.png")));
            statLogos.put(Stat.healingBonus, ImageIO.read(new File("res/healingBonus.png")));
            statLogos.put(Stat.havocBonus, ImageIO.read(new File("res/havocBonus.png")));
            statLogos.put(Stat.spectroBonus, ImageIO.read(new File("res/spectroBonus.png")));
            statLogos.put(Stat.fusionBonus, ImageIO.read(new File("res/fusionBonus.png")));
            statLogos.put(Stat.glacioBonus, ImageIO.read(new File("res/glacioBonus.png")));
            statLogos.put(Stat.electroBonus, ImageIO.read(new File("res/electroBonus.png")));
            statLogos.put(Stat.aeroBonus, ImageIO.read(new File("res/aeroBonus.png")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
