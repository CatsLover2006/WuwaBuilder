package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import ca.litten.discordbot.wuwabuilder.wuwa.Level;
import ca.litten.discordbot.wuwabuilder.wuwa.Weapon;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static ca.litten.discordbot.wuwabuilder.WuwaDatabase.*;

public class CacheFromHakushin {
    public static void main(String[] args) throws Throwable {
        initFromHakushin();
        JSONObject offlineDB = new JSONObject();
        JSONArray dbSubList = new JSONArray();
        JSONObject objectProcessor;
        JSONArray arrayProcessor;
        File of;
        for (long sonata : sonataImageCache.keySet()) {
            objectProcessor = new JSONObject();
            arrayProcessor = new JSONArray();
            objectProcessor.put("sonata", sonata);
            objectProcessor.put("name", sonataNameCache.get(sonata));
            of = new File("res/sonata/" + sonata + ".png");
            ImageIO.write(sonataImageCache.get(sonata), "png", of);
            objectProcessor.put("imageLoc", "sonata/" + sonata + ".png");
            for (long echoID : sonataEchoCache.get(sonata))
                arrayProcessor.put(echoID);
            objectProcessor.put("echoes", arrayProcessor);
            dbSubList.put(objectProcessor);
        }
        offlineDB.put("sonata", dbSubList);
        dbSubList = new JSONArray();
        for (long echo : echoImageCache.keySet()) {
            objectProcessor = new JSONObject();
            objectProcessor.put("echo", echo);
            objectProcessor.put("name", echoNameCache.get(echo));
            of = new File("res/echo/" + echo + ".png");
            ImageIO.write(echoImageCache.get(echo), "png", of);
            objectProcessor.put("imageLoc", "echo/" + echo + ".png");
            dbSubList.put(objectProcessor);
        }
        offlineDB.put("echo", dbSubList);
        dbSubList = new JSONArray();
        for (long weaponID : Weapon.weaponIDs()) {
            Weapon weapon = Weapon.getWeaponByID(weaponID);
            objectProcessor = new JSONObject();
            objectProcessor.put("weapon", weaponID);
            objectProcessor.put("name", weapon.getName());
            objectProcessor.put("mainStat", weapon.getMainStat().toString());
            arrayProcessor = new JSONArray();
            for (Level level : Level.values()) {
                arrayProcessor.put(weapon.getMainStatForLevel(level));
            }
            objectProcessor.put("mainStatMag", arrayProcessor);
            objectProcessor.put("subStat", weapon.getSubStat().toString());
            arrayProcessor = new JSONArray();
            for (Level level : Level.values()) {
                arrayProcessor.put(weapon.getSubStatForLevel(level));
            }
            objectProcessor.put("subStatMag", arrayProcessor);
            arrayProcessor = new JSONArray();
            for (int i = 0; i < 5; i++) {
                JSONObject statBufObj = new JSONObject();
                for (StatPair pair : weapon.getUnconditionalBuffs(i)) {
                    statBufObj.put(pair.stat.toString(), pair.value);
                }
                arrayProcessor.put(statBufObj);
            }
            objectProcessor.put("buffs", arrayProcessor);
            objectProcessor.put("stars", weapon.getStarCount());
            of = new File("res/weapon/" + weaponID + ".png");
            ImageIO.write(weapon.getImage(), "png", of);
            objectProcessor.put("imageLoc", "weapon/" + weaponID + ".png");
            dbSubList.put(objectProcessor);
        }
        offlineDB.put("weapon", dbSubList);
        dbSubList = new JSONArray();
        for (long characterID : Character.characterIds()) {
            Character character = Character.getCharacterByID(characterID);
            objectProcessor = new JSONObject();
            objectProcessor.put("character", characterID);
            objectProcessor.put("name", character.getName());
            objectProcessor.put("stars", character.getStarCount());
            of = new File("res/character/" + characterID + "/main.png");
            if (!of.exists()) Files.createDirectories(of.toPath());
            ImageIO.write(character.getImage(), "png", of);
            objectProcessor.put("imageLoc", "character/" + characterID + "/main.png");
            arrayProcessor = new JSONArray();
            for (int i = 0; i < 6; i++) {
                arrayProcessor.put(character.getChainName(i));
            }
            objectProcessor.put("chainNames", arrayProcessor);
            arrayProcessor = new JSONArray();
            for (int i = 0; i < 8; i++) {
                arrayProcessor.put(character.getSkillName(i));
            }
            objectProcessor.put("skillNames", arrayProcessor);
            arrayProcessor = new JSONArray();
            for (int i = 0; i < 8; i++) {
                JSONObject statObj = new JSONObject();
                Character.MinorStatBuff statBuff = character.getStatBuf(i);
                statObj.put("stat", statBuff.stat.stat.toString());
                statObj.put("mag", statBuff.stat.value);
                of = new File("res/character/" + characterID + "/sb/" + i + ".png");
                if (!of.exists()) Files.createDirectories(of.toPath());
                ImageIO.write(statBuff.image, "png", of);
                statObj.put("img", "character/" + characterID + "/sb/" + i + ".png");
                arrayProcessor.put(statObj);
            }
            objectProcessor.put("minorStats", arrayProcessor);
            arrayProcessor = new JSONArray();
            for (int i = 0; i < 6; i++) {
                of = new File("res/character/" + characterID + "/chain/" + i + ".png");
                if (!of.exists()) Files.createDirectories(of.toPath());
                ImageIO.write(character.getChain(i), "png", of);
                arrayProcessor.put("character/" + characterID + "/chain/" + i + ".png");
            }
            objectProcessor.put("chainImg", arrayProcessor);
            arrayProcessor = new JSONArray();
            for (int i = 0; i < 8; i++) {
                of = new File("res/character/" + characterID + "/skill/" + i + ".png");
                if (!of.exists()) Files.createDirectories(of.toPath());
                ImageIO.write(character.getSkill(i), "png", of);
                arrayProcessor.put("character/" + characterID + "/skill/" + i + ".png");
            }
            objectProcessor.put("skillImg", arrayProcessor);
            objectProcessor.put("element", character.getElement().toString());
            arrayProcessor = new JSONArray();
            for (Level level : Level.values()) {
                JSONArray stat = new JSONArray();
                stat.put(character.getAtkForLevel(level));
                stat.put(character.getHpForLevel(level));
                stat.put(character.getDefForLevel(level));
                arrayProcessor.put(stat);
            }
            objectProcessor.put("stats", arrayProcessor);
            dbSubList.put(objectProcessor);
        }
        offlineDB.put("character", dbSubList);
        of = new File("res/db.json");
        FileOutputStream outputStream = new FileOutputStream(of);
        outputStream.write(offlineDB.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
