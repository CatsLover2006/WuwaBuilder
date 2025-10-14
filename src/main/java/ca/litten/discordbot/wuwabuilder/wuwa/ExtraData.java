package ca.litten.discordbot.wuwabuilder.wuwa;

import javax.imageio.ImageIO;

import static ca.litten.discordbot.wuwabuilder.WuwaDatabaseLoader.StatPair;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

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
    public static final HashMap<Long, StatPair[][]> weaponPassiveBuffs = new HashMap<>();
    private static final HashMap<Sonata, StatPair[]> sonataBuffs = new HashMap<>();
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
    
    private static final StatPair glacio10 = new StatPair(Stat.glacioBonus, 10);
    private static final StatPair glacio12 = new StatPair(Stat.glacioBonus, 12);
    private static final StatPair glacio15 = new StatPair(Stat.glacioBonus, 15);
    private static final StatPair glacio18 = new StatPair(Stat.glacioBonus, 18);
    private static final StatPair glacio21 = new StatPair(Stat.glacioBonus, 21);
    private static final StatPair glacio24 = new StatPair(Stat.glacioBonus, 24);
    private static final StatPair spectro10 = new StatPair(Stat.spectroBonus, 10);
    private static final StatPair spectro12 = new StatPair(Stat.spectroBonus, 12);
    private static final StatPair spectro15 = new StatPair(Stat.spectroBonus, 15);
    private static final StatPair spectro18 = new StatPair(Stat.spectroBonus, 18);
    private static final StatPair spectro21 = new StatPair(Stat.spectroBonus, 21);
    private static final StatPair spectro24 = new StatPair(Stat.spectroBonus, 24);
    private static final StatPair electro10 = new StatPair(Stat.electroBonus, 10);
    private static final StatPair electro12 = new StatPair(Stat.electroBonus, 12);
    private static final StatPair electro15 = new StatPair(Stat.electroBonus, 15);
    private static final StatPair electro18 = new StatPair(Stat.electroBonus, 18);
    private static final StatPair electro21 = new StatPair(Stat.electroBonus, 21);
    private static final StatPair electro24 = new StatPair(Stat.electroBonus, 24);
    private static final StatPair aero10 = new StatPair(Stat.aeroBonus, 10);
    private static final StatPair aero12 = new StatPair(Stat.aeroBonus, 12);
    private static final StatPair aero15 = new StatPair(Stat.aeroBonus, 15);
    private static final StatPair aero18 = new StatPair(Stat.aeroBonus, 18);
    private static final StatPair aero21 = new StatPair(Stat.aeroBonus, 21);
    private static final StatPair aero24 = new StatPair(Stat.aeroBonus, 24);
    private static final StatPair fusion10 = new StatPair(Stat.fusionBonus, 10);
    private static final StatPair fusion12 = new StatPair(Stat.fusionBonus, 12);
    private static final StatPair fusion15 = new StatPair(Stat.fusionBonus, 15);
    private static final StatPair fusion18 = new StatPair(Stat.fusionBonus, 18);
    private static final StatPair fusion21 = new StatPair(Stat.fusionBonus, 21);
    private static final StatPair fusion24 = new StatPair(Stat.fusionBonus, 24);
    private static final StatPair havoc10 = new StatPair(Stat.havocBonus, 10);
    private static final StatPair havoc12 = new StatPair(Stat.havocBonus, 12);
    private static final StatPair havoc15 = new StatPair(Stat.havocBonus, 15);
    private static final StatPair havoc18 = new StatPair(Stat.havocBonus, 18);
    private static final StatPair havoc21 = new StatPair(Stat.havocBonus, 21);
    private static final StatPair havoc24 = new StatPair(Stat.havocBonus, 24);
    private static final StatPair heal10 = new StatPair(Stat.healingBonus, 10);
    private static final StatPair er10 = new StatPair(Stat.energyRegen, 10);
    private static final StatPair er16 = new StatPair(Stat.energyRegen, 16);
    private static final StatPair atk4 = new StatPair(Stat.percentATK, 4);
    private static final StatPair atk5 = new StatPair(Stat.percentATK, 5);
    private static final StatPair atk6 = new StatPair(Stat.percentATK, 6);
    private static final StatPair atk7 = new StatPair(Stat.percentATK, 7);
    private static final StatPair atk8 = new StatPair(Stat.percentATK, 8);
    private static final StatPair atk10 = new StatPair(Stat.percentATK, 10);
    private static final StatPair atk12 = new StatPair(Stat.percentATK, 12);
    private static final StatPair atk15 = new StatPair(Stat.percentATK, 15);
    private static final StatPair atk18 = new StatPair(Stat.percentATK, 18);
    private static final StatPair atk21 = new StatPair(Stat.percentATK, 21);
    private static final StatPair atk24 = new StatPair(Stat.percentATK, 24);
    private static final StatPair hp12 = new StatPair(Stat.percentHP, 12);
    private static final StatPair hp15 = new StatPair(Stat.percentHP, 15);
    private static final StatPair hp18 = new StatPair(Stat.percentHP, 18);
    private static final StatPair hp21 = new StatPair(Stat.percentHP, 21);
    private static final StatPair hp24 = new StatPair(Stat.percentHP, 24);
    private static final StatPair skill12 = new StatPair(Stat.skillBonus, 12);
    private static final StatPair skill15 = new StatPair(Stat.skillBonus, 15);
    private static final StatPair skill18 = new StatPair(Stat.skillBonus, 18);
    private static final StatPair skill21 = new StatPair(Stat.skillBonus, 21);
    private static final StatPair skill24 = new StatPair(Stat.skillBonus, 24);
    private static final StatPair basic12 = new StatPair(Stat.basicBonus, 12);
    private static final StatPair basic15 = new StatPair(Stat.basicBonus, 15);
    private static final StatPair basic18 = new StatPair(Stat.basicBonus, 18);
    private static final StatPair basic21 = new StatPair(Stat.basicBonus, 21);
    private static final StatPair basic24 = new StatPair(Stat.basicBonus, 24);
    private static final StatPair heavy12 = new StatPair(Stat.heavyBonus, 12);
    private static final StatPair heavy15 = new StatPair(Stat.heavyBonus, 15);
    private static final StatPair heavy18 = new StatPair(Stat.heavyBonus, 18);
    private static final StatPair heavy21 = new StatPair(Stat.heavyBonus, 21);
    private static final StatPair heavy24 = new StatPair(Stat.heavyBonus, 24);
    private static final StatPair ult12 = new StatPair(Stat.ultBonus, 12);
    private static final StatPair ult15 = new StatPair(Stat.ultBonus, 15);
    private static final StatPair ult18 = new StatPair(Stat.ultBonus, 18);
    private static final StatPair ult21 = new StatPair(Stat.ultBonus, 21);
    private static final StatPair ult24 = new StatPair(Stat.ultBonus, 24);
    private static final StatPair critRate8 = new StatPair(Stat.critRate, 8);
    private static final StatPair critRate10 = new StatPair(Stat.critRate, 10);
    private static final StatPair critRate12 = new StatPair(Stat.critRate, 12);
    private static final StatPair critRate14 = new StatPair(Stat.critRate, 14);
    private static final StatPair critRate16 = new StatPair(Stat.critRate, 16);
    
    private static final StatPair[][] attibuteBonus5star = new StatPair[][]{
            {glacio12, spectro12, electro12, aero12, fusion12, havoc12},
            {glacio15, spectro15, electro15, aero15, fusion15, havoc15},
            {glacio18, spectro18, electro18, aero18, fusion18, havoc18},
            {glacio21, spectro21, electro21, aero21, fusion21, havoc21},
            {glacio24, spectro24, electro24, aero24, fusion24, havoc24}};
    private static final StatPair[][] oneStar = new StatPair[][]{{atk4},{atk5},{atk6},{atk7},{atk8}};
    private static final StatPair[][] twoStar = new StatPair[][]{{atk5},
            {new StatPair(Stat.percentATK, 6.25f)},
            {new StatPair(Stat.percentATK, 7.5f)},
            {new StatPair(Stat.percentATK, 8.75f)},{atk10}};
    private static final StatPair[][] er5star = new StatPair[][]{
            {new StatPair(Stat.energyRegen, 12.8f)},{er16},
            {new StatPair(Stat.energyRegen, 19.2f)},
            {new StatPair(Stat.energyRegen, 22.4f)},
            {new StatPair(Stat.energyRegen, 25.6f)}};
    private static final StatPair[][] atk5star = new StatPair[][]{{atk12},{atk15},{atk18},{atk21},{atk24}};
    private static final StatPair[][] cr5star =
            new StatPair[][]{{critRate8},{critRate10},{critRate12},{critRate14},{critRate16}};
    private static final StatPair[][] skill3star =
            new StatPair[][]{{skill12},{skill15},{skill18},{skill21},{skill24}};
    private static final StatPair[][] hp5star = new StatPair[][]{{hp12},{hp15},{hp18},{hp21},{hp24}};
    private static final StatPair[][] basicHeavy3star = new StatPair[][]{
            {basic12, heavy12},{basic15, heavy15},{basic18, heavy18},{basic21, heavy21},{basic24, heavy24}};
    
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
        // Sonata Buffs
        sonataBuffs.put(new Sonata(1, 2), new StatPair[]{glacio10});
        sonataBuffs.put(new Sonata(2, 2), new StatPair[]{fusion10});
        sonataBuffs.put(new Sonata(3, 2), new StatPair[]{electro10});
        sonataBuffs.put(new Sonata(4, 2), new StatPair[]{aero10});
        sonataBuffs.put(new Sonata(5, 2), new StatPair[]{spectro10});
        sonataBuffs.put(new Sonata(6, 2), new StatPair[]{havoc10});
        sonataBuffs.put(new Sonata(7, 2), new StatPair[]{heal10});
        sonataBuffs.put(new Sonata(8, 2), new StatPair[]{er10});
        sonataBuffs.put(new Sonata(9, 2), new StatPair[]{atk10});
        sonataBuffs.put(new Sonata(10, 2), new StatPair[]{skill12});
        sonataBuffs.put(new Sonata(11, 2), new StatPair[]{spectro10});
        sonataBuffs.put(new Sonata(12, 2), new StatPair[]{havoc10});
        sonataBuffs.put(new Sonata(13, 2), new StatPair[]{er10});
        sonataBuffs.put(new Sonata(14, 2), new StatPair[]{er10});
        sonataBuffs.put(new Sonata(14, 5), new StatPair[]{atk15});
        // 15: Doesn't exist?
        sonataBuffs.put(new Sonata(16, 2), new StatPair[]{aero10});
        sonataBuffs.put(new Sonata(17, 2), new StatPair[]{aero10});
        sonataBuffs.put(new Sonata(18, 2), new StatPair[]{fusion10});
        // 19: Dream of the Lost; No unconditional bonuses
        // 20: Crown of Valor; No unconditional bonuses
        // 21: Law of Harmony; No unconditional bonuses
        // 22: Flamewing's Shadow; No unconditional bonuses
        // Weapon buffs
        weaponPassiveBuffs.put(21010011L, oneStar); // Training Broadblade
        weaponPassiveBuffs.put(21010012L, twoStar); // Tyro Broadblade
        weaponPassiveBuffs.put(21010015L, er5star); // Lustrous Razor
        weaponPassiveBuffs.put(21010016L, attibuteBonus5star); // Verdant Summit
        weaponPassiveBuffs.put(21010026L, attibuteBonus5star); // Ages of Harvest
        weaponPassiveBuffs.put(21010036L, atk5star); // Wildfire Mark
        weaponPassiveBuffs.put(21010046L, atk5star); // Thunderflare Dominion
        weaponPassiveBuffs.put(21010053L, basicHeavy3star); // Guardian Broadblade
        weaponPassiveBuffs.put(21020011L, oneStar); // Training Sword
        weaponPassiveBuffs.put(21020012L, twoStar); // Tyro Sword
        weaponPassiveBuffs.put(21020015L, er5star); // Emerald of Genesis
        weaponPassiveBuffs.put(21020016L, atk5star); // Blazing Brilliance
        weaponPassiveBuffs.put(21020036L, cr5star); // Unflickering Valor
        weaponPassiveBuffs.put(21020053L, skill3star); // Guardian Sword
        weaponPassiveBuffs.put(21020056L, hp5star); // Defier's Thorn
        weaponPassiveBuffs.put(21020066L, atk5star); // Emerald Sentence
        weaponPassiveBuffs.put(21030011L, oneStar); // Training Pistols
        weaponPassiveBuffs.put(21030012L, twoStar); // Tyro Pistols
        weaponPassiveBuffs.put(21030015L, er5star); // Static Mist
        weaponPassiveBuffs.put(21030016L, atk5star); // The Last Dance
        weaponPassiveBuffs.put(21030026L, atk5star); // Woodland Aria
        weaponPassiveBuffs.put(21030036L, atk5star); // Lux & Umbra
        weaponPassiveBuffs.put(21030053L, skill3star); // Guardian Pistols
        weaponPassiveBuffs.put(21040011L, oneStar); // Training Gauntlets
        weaponPassiveBuffs.put(21040012L, twoStar); // Tyro Gauntlets
        weaponPassiveBuffs.put(21040015L, er5star); // Abyss Surges
        weaponPassiveBuffs.put(21040016L, attibuteBonus5star); // Verity's Handle
        weaponPassiveBuffs.put(21040026L, atk5star); // Tragicomedy
        weaponPassiveBuffs.put(21040036L, atk5star); // Blazing Justice
        weaponPassiveBuffs.put(21040046L, atk5star); // Moongazer's Sigil
        weaponPassiveBuffs.put(21040053L, new StatPair[][]{{ult12},{ult15},{ult18},{ult21},{ult24}}); // Guardian Gauntlets
        weaponPassiveBuffs.put(21050011L, oneStar); // Training Rectifier
        weaponPassiveBuffs.put(21050012L, twoStar); // Tyro Rectifier
        weaponPassiveBuffs.put(21050015L, er5star); // Cosmic Ripples
        weaponPassiveBuffs.put(21050016L, attibuteBonus5star); // Stringmaster
        weaponPassiveBuffs.put(21050026L, atk5star); // Rime-Draped Sprouts
        weaponPassiveBuffs.put(21050036L, hp5star); // Stellar Symphony
        weaponPassiveBuffs.put(21050046L, atk5star); // Luminous Hymn
        weaponPassiveBuffs.put(21050053L, basicHeavy3star); // Guardian Rectifier
        weaponPassiveBuffs.put(21050056L, atk5star); // Whispers of Sirens
        weaponPassiveBuffs.put(21050066L, new StatPair[][]{{atk12},{atk15},{atk18},{atk21},{atk24}}); // Lethean Elegy
    }
}
