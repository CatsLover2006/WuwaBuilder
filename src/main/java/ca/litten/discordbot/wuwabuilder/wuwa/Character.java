package ca.litten.discordbot.wuwabuilder.wuwa;

import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ca.litten.discordbot.wuwabuilder.HakushinInterface.baseURL;

public class Character {
    private static final Map<Long, Character> characters = new HashMap<>();
    
    private final Map <Level, Float> atkMagnitude;
    private final Map <Level, Float> hpMagnitude;
    private final Map <Level, Float> defMagnitude;
    private BufferedImage image;
    private final BufferedImage[] chains;
    private final BufferedImage[] skills;
    private String name;
    private int starCount;
    private long id;
    
    private Character() {
        atkMagnitude = new HashMap<>();
        hpMagnitude = new HashMap<>();
        defMagnitude = new HashMap<>();
        chains = new BufferedImage[6];
        skills = new BufferedImage[5];
    };
    
    public static void createCharacter(JSONObject hakushinJSON) {
        Character character = new Character();
        characters.put(hakushinJSON.getLong("Id"), character);
        character.id = hakushinJSON.getLong("Id");
        character.name = hakushinJSON.getString("Name");
        character.starCount = hakushinJSON.getInt("Rarity");
        ArrayList<Thread> imageGrabberThreads = new ArrayList<>();
        try {
            String iconSubURL = hakushinJSON.getString("Background").replace("/Game/Aki", "");
            HakushinInterface.ImageGrabberThread imageGrabberThread =
                    new HakushinInterface.ImageGrabberThread(image -> character.image = image,
                            new URL(baseURL, "ww" + iconSubURL.substring(0,
                                    iconSubURL.lastIndexOf('.')) + ".webp"));
            imageGrabberThread.start();
            imageGrabberThreads.add(imageGrabberThread);
            JSONObject chainCache = hakushinJSON.getJSONObject("Chains");
            for (int i = 0; i < 6; i++) {
                String chainURL = chainCache.getJSONObject(String.valueOf(i + 1))
                        .getString("Icon").replace("/Game/Aki", "");
                int finalI = i;
                imageGrabberThread = new HakushinInterface.ImageGrabberThread(image -> character.chains[finalI] = image,
                        new URL(baseURL, "ww" + chainURL.substring(0,
                                chainURL.lastIndexOf('.')) + ".webp"));
                imageGrabberThread.start();
                imageGrabberThreads.add(imageGrabberThread);
            }
            JSONObject skillTreeCache = hakushinJSON.getJSONObject("SkillTrees");
            for (String key : skillTreeCache.keySet()) {
                JSONObject skillObject = skillTreeCache.getJSONObject(key);
                if (skillObject.getInt("NodeType") == 1) { // Forte
                    String skillURL = skillObject.getJSONObject("Skill").getString("Icon")
                            .replace("/Game/Aki", "");
                    imageGrabberThread = new HakushinInterface.ImageGrabberThread(image -> character.skills[0] = image,
                            new URL(baseURL, "ww" + skillURL.substring(0,
                                    skillURL.lastIndexOf('.')) + ".webp"));
                    imageGrabberThread.start();
                    imageGrabberThreads.add(imageGrabberThread);
                } else if (skillObject.getInt("NodeType") == 2) {
                    // 1: Basic
                    // 2: Skill
                    // 3: Liberation
                    // 4: Intro
                    String skillURL = skillObject.getJSONObject("Skill").getString("Icon")
                            .replace("/Game/Aki", "");
                    imageGrabberThread = new HakushinInterface.ImageGrabberThread(image ->
                            character.skills[skillObject.getInt("Coordinate")] = image,
                            new URL(baseURL, "ww" + skillURL.substring(0,
                                    skillURL.lastIndexOf('.')) + ".webp"));
                    imageGrabberThread.start();
                    imageGrabberThreads.add(imageGrabberThread);
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JSONObject statCache = hakushinJSON.getJSONObject("Stats");
        JSONObject ascensionCache;
        char levelLookup;
        HakushinInterface.StatPair statPair;
        int j = 1;
        for (int i = 0; statCache.has(String.valueOf(i)); i++) {
            ascensionCache = statCache.getJSONObject(String.valueOf(i));
            levelLookup = "abcdefghi".charAt(i);
            for (; ascensionCache.has(String.valueOf(j)); j++) {
                Level level = Level.valueOf(levelLookup + String.valueOf(j));
                JSONObject levelCache = ascensionCache.getJSONObject(String.valueOf(j));
                character.atkMagnitude.put(level, levelCache.getFloat("Atk"));
                character.defMagnitude.put(level, levelCache.getFloat("Def"));
                character.hpMagnitude.put(level, levelCache.getFloat("Life"));
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
    
    public static Character getCharacterByID(long id) {
        return characters.get(id);
    }
    
    public static Character getCharacterByName(@NotNull String name) {
        for (Character character : characters.values()) {
            if (character.name.equals(name)) return character;
        }
        return null;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public String getName() {
        return name;
    }
    
    public float getAtkForLevel(Level level) {
        return atkMagnitude.get(level);
    }
    
    public float getHpForLevel(Level level) {
        return hpMagnitude.get(level);
    }
    
    public float getDefForLevel(Level level) {
        return defMagnitude.get(level);
    }
}
