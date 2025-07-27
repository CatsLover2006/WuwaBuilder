package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import ca.litten.discordbot.wuwabuilder.wuwa.Stat;
import ca.litten.discordbot.wuwabuilder.wuwa.Weapon;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.net.HttpURLConnection;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class HakushinInterface {
    private static final Map<Long, BufferedImage> sonataImageCacheMutable = new HashMap<>();
    private static final Map<Long, BufferedImage> echoImageCacheMutable = new HashMap<>();
    private static final Map<Long, String> sonataNameCacheMutable = new HashMap<>();
    private static final Map<Long, String> echoNameCacheMutable = new HashMap<>();
    private static final Map<Long, ArrayList<Long>> sonataEchoCacheMutable = new HashMap<>();
    
    
    public static final Map<Long, BufferedImage> sonataImageCache = Collections.unmodifiableMap(sonataImageCacheMutable);
    public static final Map<Long, BufferedImage> echoImageCache = Collections.unmodifiableMap(echoImageCacheMutable);
    public static final Map<Long, String> sonataNameCache = Collections.unmodifiableMap(sonataNameCacheMutable);
    public static final Map<Long, String> echoNameCache = Collections.unmodifiableMap(echoNameCacheMutable);
    public static final Map<Long, ArrayList<Long>> sonataEchoCache = Collections.unmodifiableMap(sonataEchoCacheMutable);
    
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
                        urlConnection.setRequestProperty("User-Agent", "I_AM_A_DISCORD_BOT");
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
    
    public static void init() {
        try { // Default URL
            init(new URL("https://api.hakush.in/"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void init(URL baseURL) {
        HakushinInterface.baseURL = baseURL;
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
            //System.out.println("Caching echo " + echo + "...");
            try {
                cacheData = connect(new URL(baseURL, "ww/data/en/echo/" + echo + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JSONObject echoObject = new JSONObject(new BufferedReader(
                    new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n")));
            echoNameCacheMutable.put(echoObject.getLong("Id"), echoObject.getString("Name"));
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
            //System.out.println("Caching weapon " + weapon + "...");
            try {
                cacheData = connect(new URL(baseURL, "ww/data/en/weapon/" + weapon + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Weapon.createWeapon(new JSONObject(new BufferedReader(
                    new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"))));
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
            //System.out.println("Caching character " + character + "...");
            try {
                cacheData = connect(new URL(baseURL, "ww/data/en/character/" + character + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Character.createCharacter(new JSONObject(new BufferedReader(
                    new InputStreamReader(cacheData.inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"))));
        }
        for (Thread thread : imageGrabberThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                return new StatPair(json.getBoolean("IsRatio") ? Stat.percentATK : Stat.flatATK,
                        json.getFloat("Value"));
            case "DEF":
                if (json.getBoolean("IsPercent"))
                    return new StatPair(Stat.percentDEF, json.getFloat("Value") / 100);
                return new StatPair(json.getBoolean("IsRatio") ? Stat.percentDEF : Stat.flatDEF,
                        json.getFloat("Value"));
            case "HP":
                if (json.getBoolean("IsPercent"))
                    return new StatPair(Stat.percentHP, json.getFloat("Value") / 100);
                return new StatPair(json.getBoolean("IsRatio") ? Stat.percentHP : Stat.flatHP,
                        json.getFloat("Value"));
        }
        return new StatPair(Stat.flatATK, 0); // Default
    }
}
