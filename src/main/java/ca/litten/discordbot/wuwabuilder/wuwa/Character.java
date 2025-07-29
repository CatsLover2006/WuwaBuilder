package ca.litten.discordbot.wuwabuilder.wuwa;

import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.parser.BuildParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ca.litten.discordbot.wuwabuilder.HakushinInterface.baseURL;

public class Character {
    private static final Map<Long, Character> characters = new HashMap<>();
    
    private final Map <Level, Float> atkMagnitude;
    private final Map <Level, Float> hpMagnitude;
    private final Map <Level, Float> defMagnitude;
    private BufferedImage image;
    private final BufferedImage[] chains;
    private final BufferedImage[] skills;
    private final MinorStatBuff[] minorStatBuffs;
    private String name;
    private int starCount;
    private long id;
    
    public static class MinorStatBuff {
        public HakushinInterface.StatPair stat;
        public BufferedImage image;
    }
    
    private static class StatBufPassalong {
        public MinorStatBuff statBuff;
        public int pointedAt;
    }
    
    private Character() {
        atkMagnitude = new HashMap<>();
        hpMagnitude = new HashMap<>();
        defMagnitude = new HashMap<>();
        chains = new BufferedImage[6];
        skills = new BufferedImage[8];
        minorStatBuffs = new MinorStatBuff[8];
    };
    
    public static void createCharacter(JSONObject hakushinJSON) {
        Character character = new Character();
        characters.put(hakushinJSON.getLong("Id"), character);
        character.id = hakushinJSON.getLong("Id");
        character.name = hakushinJSON.getString("Name");
        if (character.name.contains("Rover:")) {
            character.name = hakushinJSON.getJSONObject("CharaInfo").getString("Sex").substring(0, 1)
                    + character.name;
        }
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
            Set<String> skillTreeCacheKeys = skillTreeCache.keySet();
            Object[] info = new Object[skillTreeCacheKeys.size()];
            for (int i = 0; i < skillTreeCacheKeys.size(); i++) info[i] = null;
            for (String key : skillTreeCacheKeys) {
                JSONObject skillObject = skillTreeCache.getJSONObject(key);
                switch (skillObject.getInt("NodeType")) {
                    case 1: { // Forte
                        String skillURL = skillObject.getJSONObject("Skill").getString("Icon")
                                .replace("/Game/Aki", "");
                        imageGrabberThread = new HakushinInterface.ImageGrabberThread(image -> character.skills[0] = image,
                                new URL(baseURL, "ww" + skillURL.substring(0,
                                        skillURL.lastIndexOf('.')) + ".webp"));
                        imageGrabberThread.start();
                        imageGrabberThreads.add(imageGrabberThread);
                        break;
                    }
                    case 2: {
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
                        info[Integer.parseInt(key) - 1] = skillObject.getInt("Coordinate");
                        break;
                    }
                    case 3: {
                        // 1: Outro
                        // 2: Inherent 1
                        // 3: Inherent 2
                        String skillURL = skillObject.getJSONObject("Skill").getString("Icon")
                                .replace("/Game/Aki", "");
                        imageGrabberThread = new HakushinInterface.ImageGrabberThread(image ->
                                character.skills[skillObject.getInt("Coordinate") + 4] = image,
                                new URL(baseURL, "ww" + skillURL.substring(0,
                                        skillURL.lastIndexOf('.')) + ".webp"));
                        imageGrabberThread.start();
                        imageGrabberThreads.add(imageGrabberThread);
                        break;
                    }
                    case 4: {
                        MinorStatBuff statBuff = new MinorStatBuff();
                        JSONObject skillObj = skillObject.getJSONObject("Skill");
                        String skillURL = skillObj.getString("Icon").replace("/Game/Aki", "");
                        imageGrabberThread = new HakushinInterface.ImageGrabberThread(image ->
                                statBuff.image = image,
                                new URL(baseURL, "ww" + skillURL.substring(0,
                                        skillURL.lastIndexOf('.')) + ".webp"));
                        imageGrabberThread.start();
                        imageGrabberThreads.add(imageGrabberThread);
                        statBuff.stat = BuildParser.readStat(skillObj.getString("Name")
                                        .replace("+","")
                                        .replace(" Up", ""),
                                skillObj.getJSONArray("Param").getString(0));
                        StatBufPassalong passer = new StatBufPassalong();
                        passer.statBuff = statBuff;
                        passer.pointedAt = skillObject.getJSONArray("ParentNodes").getInt(0) - 1;
                        info[Integer.parseInt(key) - 1] = passer; // Pass it on!
                    }
                }
            }
            for (int i = 0; i < skillTreeCacheKeys.size(); i++) {
                if (info[i] == null || info[i] instanceof Integer) continue;
                if (info[i] instanceof StatBufPassalong) {
                    Object pointedAt = info[((StatBufPassalong) info[i]).pointedAt];
                    if (pointedAt instanceof Integer) {
                        switch ((Integer) pointedAt) {
                            case 1: { // 1: Basic
                                character.minorStatBuffs[0] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                            case 2: { // 2: Skill
                                character.minorStatBuffs[2] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                            case 3: { // 3: Liberation
                                character.minorStatBuffs[4] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                            case 4: { // 4: Intro
                                character.minorStatBuffs[6] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                        }
                    } else { // It's pointed at a stat buff
                        switch ((Integer) info[((StatBufPassalong) pointedAt).pointedAt]) {
                            case 1: { // 1: Basic
                                character.minorStatBuffs[1] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                            case 2: { // 2: Skill
                                character.minorStatBuffs[3] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                            case 3: { // 3: Liberation
                                character.minorStatBuffs[5] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                            case 4: { // 4: Intro
                                character.minorStatBuffs[7] = ((StatBufPassalong) info[i]).statBuff;
                                break;
                            }
                        }
                    }
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
    
    public BufferedImage getChain(int index) {
        return chains[index];
    }
    
    public BufferedImage getSkill(int index) {
        return skills[index];
    }
    
    public MinorStatBuff getStatBuf(int index) {
        return minorStatBuffs[index];
    }
}
