package ca.litten.discordbot.wuwabuilder.bot;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebhookHandler {
    private final HttpServer server;
    private final JDA jda;
    private final String appID;
    private final Ed25519PublicKeyParameters verifier;
    private static final byte[] verifyFailed = "{\"status\":\"denied\",\"body\":\"invalid request signature\"}"
            .getBytes(StandardCharsets.UTF_8);
    private static final byte[] requestRecieved = "{\"status\":\"accepted\",\"body\":\"request received successfully\"}"
            .getBytes(StandardCharsets.UTF_8);
    
    public WebhookHandler(JDA jda, String appID, int port, String pubKey) throws IOException {
        this.jda = jda;
        this.appID = appID;
        verifier = new Ed25519PublicKeyParameters(DatatypeConverter.parseHexBinary(pubKey));
        server = HttpServer.create(new InetSocketAddress(port), -1);
        server.createContext("/", exchange -> {
            Headers headers = exchange.getRequestHeaders();
            Headers outHeaders = exchange.getResponseHeaders();
            outHeaders.set("Content-Type", "application/json");
            StringBuilder body = new StringBuilder();
            // First, we verify the signature
            try {
                String sig = "";
                String timestamp = "";
                for (String key : headers.keySet()) {
                    if (key.equalsIgnoreCase("X-signature-timestamp")) {
                        timestamp = headers.getFirst(key);
                    }
                    if (key.equalsIgnoreCase("X-signature-ed25519")) {
                        sig = headers.getFirst(key);
                    }
                }
                InputStream bodyStream = exchange.getRequestBody();
                byte[] readData = new byte[256];
                int read = bodyStream.read(readData);
                while (read != -1) {
                    body.append(new String(readData, 0, read, StandardCharsets.UTF_8));
                    read = bodyStream.read(readData);
                }
                byte[] bytes = (timestamp + body).getBytes(StandardCharsets.UTF_8);
                Ed25519Signer signer = new Ed25519Signer();
                signer.init(false, verifier);
                signer.update(bytes, 0, bytes.length); //*
                if (!signer.verifySignature(DatatypeConverter.parseHexBinary(sig))) {
                    System.out.println("Verify failed");
                    exchange.sendResponseHeaders(401, verifyFailed.length);
                    exchange.getResponseBody().write(verifyFailed);
                    exchange.close();
                    return;
                }//*/
            } catch (Exception e) {
                System.out.println(e.getMessage());
                exchange.sendResponseHeaders(401, verifyFailed.length);
                exchange.getResponseBody().write(verifyFailed);
                exchange.close();
                return;
            }
            // Now we know we're good to continue
            JSONObject json = new JSONObject(body.toString());
            for (String key : json.keySet()) System.out.println(key);
            if (!json.getString("application_id").equals(appID)) {
                System.out.println("Wrong app id");
                exchange.sendResponseHeaders(401, verifyFailed.length);
                exchange.getResponseBody().write(verifyFailed);
                exchange.close();
                return;
            }
            exchange.sendResponseHeaders(204, 0);
            exchange.close();
            if (json.getInt("type") == 0) return; // PING
            JSONObject event = json.getJSONObject("event");
            switch (event.getString("type")) {
                case "APPLICATION_AUTHORIZED": {
                    try {
                        JSONObject data = event.getJSONObject("data");
                        if (data.getInt("integration_type") == 0) return;
                        JSONObject user = data.getJSONObject("user");
                        if (user.has("bot") && user.getBoolean("bot")) return;
                        long userID = Long.parseLong(user.getString("id"));
                        jda.retrieveUserById(userID).queue(auth -> {
                            if (auth == null) return;
                            auth.openPrivateChannel().flatMap(channel ->
                                    channel.sendMessage("Welcome to Wuwa Builder!")).queue();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        server.start();
    }
}
