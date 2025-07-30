package ca.litten.discordbot.wuwabuilder.bot;

import ca.litten.discordbot.wuwabuilder.CardBuilder;
import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.parser.BuildParser;
import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.Stat;
import net.dv8tion.jda.api.components.Components;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static ca.litten.discordbot.wuwabuilder.bot.Bot.*;

public class SlashCommandListener extends ListenerAdapter {
    HashMap<String, SlashCommandBuildTracker> buildMap = new HashMap<>();
    
    private static class SlashCommandBuildTracker {
        public Build build;
        public InteractionHook hook;
        public ActionRow[] editableActionRow;
        public long owner;
        
        public SlashCommandBuildTracker(Build build, InteractionHook hook, ActionRow[] editableActionRow, long owner) {
            this.build = build;
            this.hook = hook;
            this.editableActionRow = editableActionRow;
            this.owner = owner;
        }
    }
    
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            switch (event.getName()) {
                case "generate":
                    Message.Attachment content = event.getOption("image").getAsAttachment();
                    if (!content.isImage()) {
                        event.reply("That's not an image!").queue();
                        return;
                    }
                    BufferedImage card;
                    URL url = new URL(content.getUrl());
                    try {
                        card = ImageIO.read(url);
                    } catch (Exception e) {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("User-Agent", "WuwaBot/0.0.1a");
                        urlConnection.connect(); // User Agent
                        card = ImageIO.read(urlConnection.getInputStream());
                    }
                    ReplyCallbackAction replyCallback = event.deferReply();
                    replyCallback.queue();
                    Build build = BuildParser.parseBuild(card);
                    card = cardBuilder.createCard(build);
                    ByteArrayOutputStream cardBytes = new ByteArrayOutputStream();
                    ImageIO.write(card, "png", cardBytes);
                    String name = event.getMember().getIdLong() + "." + build.character.getId() + "."
                            + event.getTimeCreated().toInstant().getEpochSecond();
                    ActionRow actionRow = ActionRow.of(Button.primary("edit.skill.minor$" + name, "Edit Stat Buffs & Inherent Skills"),
                            Button.primary("edit.chain$" + name, "Edit Resonance Chain Length & Weapon Rank"));
                    ActionRow doneEditing = ActionRow.of(Button.danger("done$" + name, "Finished Editing"));
                    ActionRow[] t = new ActionRow[]{actionRow, doneEditing};
                    buildMap.put(name, new SlashCommandBuildTracker(build, event.getHook(), t, event.getMember().getIdLong()));
                    event.getHook().sendFiles(FileUpload.fromData(cardBytes.toByteArray(), name + ".png"))
                            .setComponents(t).queue();
                    new Thread(() -> { // Prevent editing after 3 minutes
                        try {
                            Thread.sleep(1000 * 60 * 3);
                            SlashCommandBuildTracker bt = buildMap.get(name);
                            buildMap.remove(name);
                            InteractionHook hook = bt.hook;
                            bt.hook = null;
                            hook.editOriginalComponents(new ActionRow[0]).queue();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    return;
                default:
                    event.reply("Invalid command.").queue();
            }
        } catch (Throwable e) {
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement traceElement : e.getStackTrace())
                stackTrace.append(traceElement.toString()).append("\n");
            event.reply("An error occurred while processing the command!\n" +
                    "Here's the stack trace:\n" + stackTrace).queue();
        }
    }
    
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        System.out.println(event.getButton().getCustomId());
        String[] details = event.getButton().getCustomId().split("\\$");
        SlashCommandBuildTracker buildTracker = buildMap.get(details[1]);
        if (event.getMember().getIdLong() != buildTracker.owner) {
            event.reply("This build card isn't yours, silly!").setEphemeral(true).queue();
            return;
        }
        switch (details[0]) {
            case "edit.skill.minor":
                event.editComponents(getActionRowsForMinorSkills(buildTracker.build, details[1])).queue();
                return;
            case "edit.skill.minor.edit":
                if (details[2].startsWith("a")) { // Ascension Passive
                    if (details[2].equals("a1") && buildTracker.build.asensionPassive == 1)
                        buildTracker.build.asensionPassive = 0;
                    else buildTracker.build.asensionPassive = Short.parseShort(details[2].substring(1));
                } else {
                    buildTracker.build.minorSkills[Integer.parseInt(details[2])] =
                            !buildTracker.build.minorSkills[Integer.parseInt(details[2])];
                }
                event.editComponents(getActionRowsForMinorSkills(buildTracker.build, details[1])).queue();
                return;
            case "edit.chain":
                event.editComponents(getActionRowsForChains(buildTracker.build, details[1])).queue();
                return;
            case "done":
                buildTracker.editableActionRow = new ActionRow[]{};
                // We overflow since now we just ensure the build card is updated
            case "main":
            default: // OH SHIT SOMETHING BROKE
                event.editComponents(buildTracker.editableActionRow).queue();
                try { // Now we update the build card
                    BufferedImage card = cardBuilder.createCard(buildTracker.build);
                    ByteArrayOutputStream cardBytes = new ByteArrayOutputStream();
                    ImageIO.write(card, "png", cardBytes);
                    String name = event.getMember().getIdLong() + "." + buildTracker.build.character.getId() + "."
                            + event.getTimeCreated().toInstant().getEpochSecond();
                    buildTracker.hook.editOriginalAttachments(FileUpload.fromData(cardBytes.toByteArray(), name + ".png")).queue();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }
    
    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        System.out.println(event.getComponentId());
        String[] details = event.getComponentId().split("\\$");
        SlashCommandBuildTracker buildTracker = buildMap.get(details[1]);
        if (event.getMember().getIdLong() != buildTracker.owner) {
            event.reply("This build card isn't yours, silly!").setEphemeral(true).queue();
            return;
        }
        switch (details[0]) {
            case "edit.chain.chain":
                buildTracker.build.chainLength = Integer.parseInt(event.getValues().get(0));
                event.editComponents(getActionRowsForChains(buildTracker.build, details[1])).queue();
                return;
            case "edit.chain.rank":
                buildTracker.build.weaponRank = Integer.parseInt(event.getValues().get(0));
                event.editComponents(getActionRowsForChains(buildTracker.build, details[1])).queue();
                return;
        }
    }
}
