package ca.litten.discordbot.wuwabuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.stream.Collectors;

public class OfflineImageManager {
    private static class ImageData {
        public BufferedImage image;
        public Instant accessTime;
        public String fileRef;
    }
    
    private static final int MAX_LOADED_IMAGES = 128;
    
    private static HashMap<String, ImageData> imageMap = new HashMap<>();
    private static HashMap<String, String> imageLocationMap = new HashMap<>();
    
    public static void init(JSONObject offlineDB) {
        JSONArray procArr = offlineDB.getJSONArray("character");
        for (Object charJVM : procArr) {
            JSONObject chara = (JSONObject) charJVM;
            long id = chara.getLong("character");
            imageLocationMap.put(String.format("c%d", id), chara.getString("imageLoc"));
            JSONArray arr = chara.getJSONArray("chainImg");
            for (int i = 0; i < 6; i++) {
                imageLocationMap.put(String.format("c%dc%d", id, i), arr.getString(i));
            }
            arr = chara.getJSONArray("skillImg");
            for (int i = 0; i < 8; i++) {
                imageLocationMap.put(String.format("c%ds%d", id, i), arr.getString(i));
            }
        }
        procArr = offlineDB.getJSONArray("weapon");
        for (Object weapJVM : procArr) {
            JSONObject weap = (JSONObject) weapJVM;
            long id = weap.getLong("weapon");
            imageLocationMap.put(String.format("w%d", id), weap.getString("imageLoc"));
        }
    }
    
    public static BufferedImage getImage(String ref) {
        if (imageMap.containsKey(ref)) {
            ImageData dat = imageMap.get(ref);
            dat.accessTime = Instant.now();
            return dat.image;
        }
        if (!imageLocationMap.containsKey(ref)) return null;
        try {
            String loc = imageLocationMap.get(ref);
            ImageData dat = new ImageData();
            dat.fileRef = ref;
            dat.image = ImageIO.read(new File("res/" + loc));
            dat.accessTime = Instant.now();
            imageMap.put(ref, dat);
            if (imageMap.size() > MAX_LOADED_IMAGES) {
                imageMap.remove(
                        imageMap.values().stream()
                                .sorted((img1, img2) -> Math.toIntExact(img1.accessTime.toEpochMilli()
                                        - img2.accessTime.toEpochMilli())).collect(Collectors.toList())
                                .get(0).fileRef);
            }
            return dat.image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
