package ca.litten.discordbot.wuwabuilder.wuwa;

import ca.litten.discordbot.wuwabuilder.WuwaDatabaseLoader;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ca.litten.discordbot.wuwabuilder.WuwaDatabaseLoader.baseURL;
import static ca.litten.discordbot.wuwabuilder.WuwaDatabaseLoader.statValueConverter;

public class Weapon {
    private static final Map<Long, Weapon> weapons = new HashMap<>();
    
    private Stat mainStat;
    private Stat subStat;
    private final Map <Level, Float> mainStatMagnitude;
    private final Map <Level, Float> subStatMagnitude;
    private BufferedImage image;
    private String name;
    private int starCount;
    private long id;
    
    private Weapon() {
        mainStatMagnitude = new HashMap<>();
        subStatMagnitude = new HashMap<>();
    };
    
    public static void createWeaponFromHakushin(JSONObject hakushinJSON) {
        if (hakushinJSON.has("Skin") && hakushinJSON.getBoolean("Skin")) return;
        Weapon weapon = new Weapon();
        weapons.put(hakushinJSON.getLong("Id"), weapon);
        weapon.id = hakushinJSON.getLong("Id");
        weapon.name = hakushinJSON.getString("Name");
        weapon.starCount = hakushinJSON.getInt("Rarity");
        ArrayList<Thread> imageGrabberThreads = new ArrayList<>();
        String iconSubURL = hakushinJSON.getString("Icon").replace("/Game/Aki", "");
        try {
            WuwaDatabaseLoader.ImageGrabberThread imageGrabberThread =
                    new WuwaDatabaseLoader.ImageGrabberThread(image -> weapon.image = image,
                            new URL(baseURL, "ww" + iconSubURL.substring(0,
                                    iconSubURL.lastIndexOf('.')) + ".webp"));
            imageGrabberThread.start();
            imageGrabberThreads.add(imageGrabberThread);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JSONObject statCache = hakushinJSON.getJSONObject("Stats");
        JSONObject ascensionCache;
        char levelLookup;
        WuwaDatabaseLoader.StatPair statPair;
        int j = 1;
        for (int i = 0; statCache.has(String.valueOf(i)); i++) {
            ascensionCache = statCache.getJSONObject(String.valueOf(i));
            levelLookup = "abcdefghi".charAt(i);
            for (; ascensionCache.has(String.valueOf(j)); j++) {
                Level level = Level.valueOf(levelLookup + String.valueOf(j));
                JSONArray levelCache = ascensionCache.getJSONArray(String.valueOf(j));
                statPair = statValueConverter(levelCache.getJSONObject(0));
                weapon.mainStat = statPair.stat;
                weapon.mainStatMagnitude.put(level, statPair.value);
                statPair = statValueConverter(levelCache.getJSONObject(1));
                weapon.subStat = statPair.stat;
                weapon.subStatMagnitude.put(level, statPair.value);
            }
            j--;
        }
        for (Thread thread : imageGrabberThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Weapon getWeaponByID(long id) {
        return weapons.get(id);
    }
    
    public static Weapon getWeaponByName(@NotNull String name) {
        for (Weapon weapon : weapons.values()) {
            if (weapon.name.equalsIgnoreCase(name)) return weapon;
        }
        return null;
    }
    
    public Stat getMainStat() {
        return mainStat;
    }
    
    public Stat getSubStat() {
        return subStat;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    public float getMainStatForLevel(Level level) {
        return mainStatMagnitude.get(level);
    }
    
    public float getSubStatForLevel(Level level) {
        return subStatMagnitude.get(level);
    }
    
    public WuwaDatabaseLoader.StatPair[] getUnconditionalBuffs(int rank) {
        return ExtraData.weaponPassiveBuffs.getOrDefault(id, new WuwaDatabaseLoader.StatPair[5][0])[rank];
    }
    
    public int getStarCount() {
        return starCount;
    }
}
