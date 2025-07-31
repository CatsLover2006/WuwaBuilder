package ca.litten.discordbot.wuwabuilder.bot;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebhookHander {
    private HttpServer server;
    private JDA jda;
    private String appID;
    private Ed25519Signer verifier;
    private static final byte[] verifyFailed = "{\"status\":\"denied\",\"body\":\"invalid request signature\"}"
            .getBytes(StandardCharsets.UTF_8);
    private static final byte[] requestRecieved = "{\"status\":\"accepted\",\"body\":\"request received successfully\"}"
            .getBytes(StandardCharsets.UTF_8);
    public WebhookHander (JDA jda, String appID, int port, String pubKey) throws IOException {
        this.jda = jda;
        this.appID = appID;
        verifier = new Ed25519Signer();
        verifier.init(false, new Ed25519PublicKeyParameters(DatatypeConverter.parseHexBinary(pubKey)));
        server = HttpServer.create(new InetSocketAddress(port), -1);
        server.createContext("/", exchange -> {
            Headers headers = exchange.getRequestHeaders();
            Headers outHeaders = exchange.getResponseHeaders();
            outHeaders.set("Content-Type", "application/json");
            StringBuilder body = new StringBuilder();
            // First, we verify the signature
            try {
                String sig = headers.getFirst("X-Signature-Ed25519");
                String timestamp = headers.getFirst("X-Signature-Timestamp");
                InputStream bodyStream = exchange.getRequestBody();
                int read = 0;
                byte[] readData = new byte[256];
                while (read != -1) {
                    body.append(new String(readData, 0, read, StandardCharsets.UTF_8));
                    read = bodyStream.read(readData);
                }
                byte[] verifyBytes = (body + timestamp).getBytes(StandardCharsets.UTF_8);
                verifier.update(verifyBytes, 0, verifyBytes.length);
                if (!verifier.verifySignature(DatatypeConverter.parseHexBinary(sig))) {
                    exchange.sendResponseHeaders(401, verifyFailed.length);
                    exchange.getResponseBody().write(verifyFailed);
                    return;
                }//*/
            } catch (Exception e) {
                exchange.sendResponseHeaders(401, verifyFailed.length);
                exchange.getResponseBody().write(verifyFailed);
                return;
            }
            // Now we know we're good to continue
            JSONObject json = new JSONObject(body);
            if (!json.getString("application_id").equals(appID)) {
                exchange.sendResponseHeaders(401, verifyFailed.length);
                exchange.getResponseBody().write(verifyFailed);
                return;
            }
            exchange.sendResponseHeaders(204, 0);
            if (json.getInt("type") == 0) return; // PING
            JSONObject event = json.getJSONObject("event");
            switch (event.getString("type")) {
                case "APPLICATION_AUTHORIZED": {
                    JSONObject data = event.getJSONObject("data");
                    if (data.getInt("integration_type") == 0) return;
                    JSONObject user = event.getJSONObject("user");
                    System.out.println(user.toString(4));
                    if (user.getBoolean("bot")) return;
                    User auth = jda.getUserById(user.getLong("id"));
                    if (auth == null) return;
                    auth.openPrivateChannel().flatMap(channel ->
                            channel.sendMessage("Welcome to Wuwa Builder!")).queue();
                }
            }
        });
        server.start();
    }
}
