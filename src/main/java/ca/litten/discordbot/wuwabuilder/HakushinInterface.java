package ca.litten.discordbot.wuwabuilder;

import org.json.JSONObject;
import sun.net.www.protocol.http.HttpURLConnection;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HakushinInterface {
    private static final Map<Long, BufferedImage> sonataImageCache = new HashMap<>();
    private static final Map<Long, BufferedImage> echoImageCache = new HashMap<>();
    private static final Map<Long, JSONObject> echoCache = new HashMap<>();
    
    private static final URL baseURL;
    
    static {
        try {
            baseURL = new URL(new URL("https://api.hakush.in/"), "ww"); // Will put the domain in config later
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class UrlInputStreamReturnValue {
        public HttpURLConnection urlConnection;
        public InputStream inputStream;
    }
    
    private static UrlInputStreamReturnValue connect(URL url) throws IOException {
        UrlInputStreamReturnValue urlInputStreamReturnValue = new UrlInputStreamReturnValue();
        urlInputStreamReturnValue.urlConnection = (HttpURLConnection) url.openConnection();
        urlInputStreamReturnValue.urlConnection.connect();
        urlInputStreamReturnValue.inputStream = urlInputStreamReturnValue.urlConnection.getInputStream();
        return urlInputStreamReturnValue;
    }
    
    public static void init() {
        UrlInputStreamReturnValue echoCacheData;
        System.out.println("Caching echo data and sonata images...");
        try {
            echoCacheData = connect(new URL(baseURL, "data/echo.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject echoCacheObject = new JSONObject(new BufferedReader(
                new InputStreamReader(echoCacheData.inputStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n")));
        echoCacheData.urlConnection.disconnect();
        for (Iterator<String> it = echoCacheObject.keys(); it.hasNext(); ) {
            String echo = it.next();
            try {
                echoCacheData = connect(new URL(baseURL, "data/en/echo/" + echo + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            echoCache.put(Long.parseLong(echo), new JSONObject(new BufferedReader(
                    new InputStreamReader(echoCacheData.inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"))));
        }
        
    }
}
