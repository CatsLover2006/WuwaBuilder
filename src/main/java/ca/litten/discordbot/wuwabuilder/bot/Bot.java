package ca.litten.discordbot.wuwabuilder.bot;

import ca.litten.discordbot.wuwabuilder.CardBuilder;
import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class Bot {
    public static CardBuilder cardBuilder;
    
    private static JSONObject loadConfig() {
        try {
            InputStream stream = Files.newInputStream(Paths.get("config.json"));
            StringBuilder string = new StringBuilder();
            byte[] readData = new byte[256];
            int read = 0;
            while (read != -1) {
                string.append(new String(readData, 0, read, StandardCharsets.UTF_8));
                read = stream.read(readData);
            }
            return new JSONObject(string.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String[] args) throws MalformedURLException {
        JSONObject config = loadConfig();
        cardBuilder = new CardBuilder(true);
        HakushinInterface.init(new URL(config.getString("hakushin")));
        JDA jda = JDABuilder.createLight(config.getString("token"), Collections.emptyList())
                .addEventListeners(new SlashCommandListener()).build();
        
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                Commands.slash("generate", "Generate build")
                        .addOption(OptionType.ATTACHMENT, "image", "Official Bot Image", true)
        );
        commands.queue();
    }
}
