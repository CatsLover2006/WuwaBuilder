package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import ca.litten.discordbot.wuwabuilder.wuwa.ExtraData;
import ca.litten.discordbot.wuwabuilder.wuwa.Stat;
import ca.litten.discordbot.wuwabuilder.wuwa.Weapon;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.HttpURLConnection;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class WuwaDatabase {
    private static final Map<Long, BufferedImage> sonataImageCacheMutable = new HashMap<>();
    private static final Map<Long, BufferedImage> echoImageCacheMutable = new HashMap<>();
    private static final Map<Long, String> sonataNameCacheMutable = new HashMap<>();
    private static final Map<Long, String> echoNameCacheMutable = new HashMap<>();
    private static final Map<Long, List<Long>> sonataEchoCacheMutable = new HashMap<>();
    
    
    public static final Map<Long, BufferedImage> sonataImageCache = Collections.unmodifiableMap(sonataImageCacheMutable);
    public static final Map<Long, BufferedImage> echoImageCache = Collections.unmodifiableMap(echoImageCacheMutable);
    public static final Map<Long, String> sonataNameCache = Collections.unmodifiableMap(sonataNameCacheMutable);
    public static final Map<Long, String> echoNameCache = Collections.unmodifiableMap(echoNameCacheMutable);
    public static final Map<Long, List<Long>> sonataEchoCache = Collections.unmodifiableMap(sonataEchoCacheMutable);
    
    public static URL baseURL;
    
    private static boolean initialized = false;
    
    private static class UrlInputStreamReturnValue {
        public HttpURLConnection urlConnection;
        public InputStream inputStream;
    }
    
    public interface ImageGrabberCallback {
        void callback(BufferedImage image);
    }
    
    public static class ImageGrabberThread extends Thread {
        private final ImageGrabberCallback callback;
        private final URL url;
        
        public ImageGrabberThread (ImageGrabberCallback callback, URL url) {
            this.callback = callback;
            this.url = url;
        }
        
        @Override
        public void run() {
            for (int i = 1; i < 10; i++) {
                try {
                    try {
                        callback.callback(ImageIO.read(url));
                    } catch (IOException e) {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("User-Agent", "WuwaBot/0.0.1a");
                        urlConnection.connect(); // User Agent
                        callback.callback(ImageIO.read(urlConnection.getInputStream()));
                    }
                    //System.out.println("Grabbed: " + url);
                    return;
                } catch (IOException e) {
                    //System.out.println("Retry " + i + ": " + url);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            //System.out.println("Failed: " + url);
        }
    }
    
    private static UrlInputStreamReturnValue connect(URL url) throws IOException {
        UrlInputStreamReturnValue urlInputStreamReturnValue = new UrlInputStreamReturnValue();
        urlInputStreamReturnValue.urlConnection = (HttpURLConnection) url.openConnection();
        urlInputStreamReturnValue.urlConnection.connect();
        urlInputStreamReturnValue.inputStream = urlInputStreamReturnValue.urlConnection.getInputStream();
        return urlInputStreamReturnValue;
    }
    
    public static void initFromHakushin() {
        try { // Default URL
            initFromHakushin(new URL("https://api.hakush.in/"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void initFromHakushin(URL baseURL) {
        WuwaDatabase.baseURL = baseURL;
        if (initialized) return; // Leave before we fuck shit up
        initialized = true;
        UrlInputStreamReturnValue cacheData;
        System.out.println("Caching echo and sonata names and images...");
        try {
            cacheData = connect(new URL(baseURL, "ww/data/echo.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject echoCacheObject = new JSONObject(new BufferedReader(
                new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n")));
        cacheData.urlConnection.disconnect();
        ArrayList<ImageGrabberThread> imageGrabberThreads = new ArrayList<>();
        ImageGrabberThread imageGrabberThread;
        for (String echo : echoCacheObject.keySet()) {
            try {
                cacheData = connect(new URL(baseURL, "ww/data/en/echo/" + echo + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JSONObject echoObject = new JSONObject(new BufferedReader(
                    new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n")));
            echoNameCacheMutable.put(echoObject.getLong("Id"), echoObject.getString("Name"));
            System.out.println("Echo " + echoObject.getLong("Id") + ": " + echoObject.getString("Name"));
            try {
                String iconSubURL = echoObject.getString("Icon").replace("/Game/Aki", "");
                imageGrabberThread = new ImageGrabberThread(image -> {
                    echoImageCacheMutable.put(echoObject.getLong("Id"), image);
                }, new URL(baseURL, "ww" + iconSubURL.substring(0, iconSubURL.lastIndexOf('.')) + ".webp"));
                imageGrabberThread.start();
                imageGrabberThreads.add(imageGrabberThread);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            JSONObject echoSonatas = echoObject.getJSONObject("Group");
            for (String sonata : echoSonatas.keySet()) {
                if (sonataEchoCacheMutable.containsKey(Long.parseLong(sonata))) {
                    sonataEchoCacheMutable.get(Long.parseLong(sonata)).add(echoObject.getLong("Id"));
                    continue;
                }
                JSONObject sonataJSON = echoSonatas.getJSONObject(sonata);
                try {
                    String iconSubURL = sonataJSON.getString("Icon").replace("/Game/Aki", "");
                    imageGrabberThread = new ImageGrabberThread(image -> {
                        sonataImageCacheMutable.put(sonataJSON.getLong("Id"), image);
                    }, new URL(baseURL, "ww" + iconSubURL.substring(0, iconSubURL.lastIndexOf('.')) + ".webp"));
                    imageGrabberThread.start();
                    imageGrabberThreads.add(imageGrabberThread);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                ArrayList<Long> sonataEcho = new ArrayList<>();
                sonataEcho.add(echoObject.getLong("Id"));
                sonataEchoCacheMutable.put(sonataJSON.getLong("Id"), sonataEcho);
                sonataNameCacheMutable.put(sonataJSON.getLong("Id"), sonataJSON.getString("Name"));
                System.out.println("Sonata " + sonataJSON.getLong("Id") + ": " + sonataJSON.getString("Name"));
            }
        }
        System.out.println("Caching weapon names, images, and stats...");
        try {
            cacheData = connect(new URL(baseURL, "ww/data/weapon.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject weaponCacheObject = new JSONObject(new BufferedReader(
                new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n")));
        cacheData.urlConnection.disconnect();
        for (String weapon : weaponCacheObject.keySet()) {
            try {
                cacheData = connect(new URL(baseURL, "ww/data/en/weapon/" + weapon + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Weapon.createWeaponFromHakushin(new JSONObject(new BufferedReader(
                    new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"))));
            System.out.print(weapon + ": ");
            Weapon weap = Weapon.getWeaponByID(Long.parseLong(weapon));
            if (weap == null) System.out.println("Skin (Skipped)");
            else System.out.println(weap.getName());
        }//*/
        System.out.println("Caching character names, images, and stats...");
        try {
            cacheData = connect(new URL(baseURL, "ww/data/character.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject characterCacheObject = new JSONObject(new BufferedReader(
                new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n")));
        cacheData.urlConnection.disconnect();
        for (String character : characterCacheObject.keySet()) {
            try {
                cacheData = connect(new URL(baseURL, "ww/data/en/character/" + character + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Character.createCharacterFromHakushin(new JSONObject(new BufferedReader(
                    new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"))));
            System.out.print(character + ": ");
            Character chara = Character.getCharacterByID(Long.parseLong(character));
            if (chara == null) System.out.println("Skin (Skipped)");
            else System.out.println(chara.getName());
        }
        for (Thread thread : imageGrabberThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void initFromOfflineDB(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[Math.toIntExact(file.length())];
            fileInputStream.read(bytes);
            JSONObject baseObject = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
            new Thread(() -> OfflineImageManager.init(baseObject)).start();
            for (Object character : baseObject.getJSONArray("character")) {
                new Thread(() -> Character.createCharacterFromOfflineDB((JSONObject) character)).start();
            }
            for (Object weapon : baseObject.getJSONArray("weapon")) {
                new Thread(() -> Weapon.createWeaponFromOfflineDB((JSONObject) weapon)).start();
            }
            for (Object sonataJVM : baseObject.getJSONArray("sonata")) {
                JSONObject sonata = (JSONObject) sonataJVM;
                long sonataID = sonata.getLong("sonata");
                sonataImageCacheMutable.put(sonataID,
                        ImageIO.read(new File("res/" + sonata.getString("imageLoc"))));
                sonataNameCacheMutable.put(sonataID, sonata.getString("name"));
                sonataEchoCacheMutable.put(sonataID, sonata.getJSONArray("echoes")
                        .toList().stream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toList()));
                for (Object buffJVM : sonata.getJSONArray("buffs")) {
                    JSONObject buff = (JSONObject) buffJVM;
                    ArrayList<StatPair> buffs = new ArrayList<>();
                    for (String key : buff.keySet()) {
                        if (key.equals("count")) continue;
                        buffs.add(new StatPair(Stat.valueOf(key), buff.getFloat(key)));
                    }
                    ExtraData.sonataBuffs.put(new ExtraData.Sonata(sonataID, buff.getInt("count")),
                            buffs.toArray(new StatPair[]{}));
                }
            }
            for (Object echoJVM : baseObject.getJSONArray("echo")) {
                JSONObject echo = (JSONObject) echoJVM;
                echoImageCacheMutable.put(echo.getLong("echo"),
                        ImageIO.read(new File("res/" + echo.getString("imageLoc"))));
                echoNameCacheMutable.put(echo.getLong("echo"), echo.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class StatPair {
        public final Stat stat;
        public final float value;
        
        public StatPair(Stat stat, float value) {
            this.stat = stat;
            this.value = value;
        }
    }
    
    public static StatPair statValueConverter(JSONObject json) {
        switch(json.getString("Name")) {
            case "Crit. DMG":
                return new StatPair(Stat.critDMG, json.getFloat("Value") / 100);
            case "Crit. Rate":
                return new StatPair(Stat.critRate, json.getFloat("Value") / 100);
            case "Energy Regen":
                return new StatPair(Stat.energyRegen, json.getFloat("Value") / 100);
            case "ATK":
                if (json.getBoolean("IsPercent"))
                    return new StatPair(Stat.percentATK, json.getFloat("Value") / 100);
                if (json.getBoolean("IsRatio"))
                    return new StatPair(Stat.percentATK, json.getFloat("Value") * 100);
                return new StatPair(Stat.flatATK, json.getFloat("Value"));
            case "DEF":
                if (json.getBoolean("IsPercent"))
                    return new StatPair(Stat.percentDEF, json.getFloat("Value") / 100);
                if (json.getBoolean("IsRatio"))
                    return new StatPair(Stat.percentDEF, json.getFloat("Value") * 100);
                return new StatPair(Stat.flatDEF, json.getFloat("Value"));
            case "HP":
                if (json.getBoolean("IsPercent"))
                    return new StatPair(Stat.percentHP, json.getFloat("Value") / 100);
                if (json.getBoolean("IsRatio"))
                    return new StatPair(Stat.percentHP, json.getFloat("Value") * 100);
                return new StatPair(Stat.flatHP, json.getFloat("Value"));
        }
        return new StatPair(Stat.flatATK, 0); // Default
    }
}
