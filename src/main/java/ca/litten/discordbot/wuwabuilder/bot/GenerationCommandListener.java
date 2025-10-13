package ca.litten.discordbot.wuwabuilder.bot;

import ca.litten.discordbot.wuwabuilder.parser.BuildParser;
import ca.litten.discordbot.wuwabuilder.wuwa.*;
import ca.litten.discordbot.wuwabuilder.wuwa.Character;
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
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import static ca.litten.discordbot.wuwabuilder.bot.Bot.*;

public class GenerationCommandListener extends ListenerAdapter {
    HashMap<String, BotCommandBuildTracker> buildMap = new HashMap<>();
    
    private class BuildCardUpdater extends Thread {
        private BotCommandBuildTracker buildTracker;
        private ActionRow[] rows;
        
        public BuildCardUpdater(BotCommandBuildTracker buildTracker) {
            this.buildTracker = buildTracker;
            this.rows = new ActionRow[0];
        }
        
        public BuildCardUpdater(BotCommandBuildTracker buildTracker, ActionRow[] rows) {
            this.buildTracker = buildTracker;
            this.rows = rows;
        }
        
        @Override
        public void run() {
            try { // Now we update the build card
                updateBuildCard(buildTracker);
                if (rows.length != 0)
                    buildTracker.hook.editOriginalComponents(rows).queue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            switch (event.getName()) {
                case "generate":
                    Message.Attachment content = event.getOption("image").getAsAttachment();
                    if (!content.isImage()) {
                        event.reply("That's not an image!").setEphemeral(true).queue();
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
                    event.deferReply().queue();
                    Build build = BuildParser.parseBuild(card);
                    String name;
                    if (event.getMember() == null) {
                        name = String.format("%xl", event.getChannelIdLong()) + "."
                                + String.format("%xl", build.character.getId()) + "."
                                + String.format("%xl", event.getTimeCreated().toInstant().getEpochSecond());
                    } else name = String.format("%xl", event.getMember().getIdLong()) +
                            "." + String.format("%xl", build.character.getId()) + "."
                            + String.format("%xl", event.getTimeCreated().toInstant().getEpochSecond());
                    ActionRow actionRow = ActionRow.of(Button.primary("chara$" + name, "Edit Resonator"),
                            Button.primary("weap$" + name, "Edit Weapon"),
                            Button.primary("echo$" + name, "Edit Echoes"),
                            Button.primary("skill$" + name, "Edit Fortes"));
                    // TODO: echo editing
                    ActionRow doneEditing = ActionRow.of(Button.danger("done$" + name, "Finished Editing"));
                    ActionRow[] t = new ActionRow[]{actionRow, doneEditing};
                    BotCommandBuildTracker buildTracker = new BotCommandBuildTracker(build, event.getHook(), t, event.getMember().getIdLong());
                    buildMap.put(name, buildTracker);
                    new BuildCardUpdater(buildTracker, t).start();
                    new Thread(() -> { // Prevent editing after 5 minutes
                        try {
                            Thread.sleep(1000 * 60 * 5);
                            BotCommandBuildTracker bt = buildMap.get(name);
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
            e.printStackTrace();
            event.getHook().editOriginal("An error occurred while processing the command!").queue();
            new Thread(() -> {
                try {
                    long piSecondsAsNanos = (long) (1000000000L * Math.PI);
                    Thread.sleep(piSecondsAsNanos / 1000000, (int) (piSecondsAsNanos % 1000000));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                event.getHook().deleteOriginal().queue();
            }).start();
        }
    }
    
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        System.out.println(event.getButton().getCustomId());
        String[] details = event.getButton().getCustomId().split("\\$");
        BotCommandBuildTracker buildTracker = buildMap.get(details[1]);
        if (event.getMember() != null && event.getMember().getIdLong() != buildTracker.owner) {
            event.reply("This build card isn't yours, silly!").setEphemeral(true).queue();
            return;
        }
        switch (details[0]) {
            case "skill.modal":
                event.replyModal(getForteEditModal(buildTracker.build, details[1])).queue();
                return;
            case "skill":
                event.editComponents(getActionRowsForSkills(buildTracker.build, details[1])).queue();
                return;
            case "skill.minor":
                if (details[2].startsWith("a")) { // Ascension Passive
                    short passiveLen = Short.parseShort(details[2].substring(1));
                    if (buildTracker.build.asensionPassive == passiveLen)
                        buildTracker.build.asensionPassive = (short)(passiveLen - 1); // Why do I need to cast this
                    else buildTracker.build.asensionPassive = passiveLen;
                } else {
                    buildTracker.build.minorSkills[Integer.parseInt(details[2])] =
                            !buildTracker.build.minorSkills[Integer.parseInt(details[2])];
                }
                event.editComponents(getActionRowsForSkills(buildTracker.build, details[1])).queue();
                return;
            case "chara":
                event.editComponents(getActionRowsForCharacter(buildTracker.build, details[1])).queue();
                return;
            case "chara.modal":
                event.replyModal(getCharacterEditModal(buildTracker.build, details[1])).queue();
                return;
            case "weap":
                event.editComponents(getActionRowsForWeapon(buildTracker.build, details[1])).queue();
                return;
            case "weap.modal":
                event.replyModal(getWeaponEditModal(buildTracker.build, details[1])).queue();
                return;
            case "echo":
                event.editComponents(getActionRowsForEchoMenu(buildTracker.build, details[1])).queue();
                return;
            case "echo.edit":
                event.editComponents(getActionRowsForEcho(buildTracker.build,
                        details[1], Short.parseShort(details[2]))).queue();
                return;
            case "echo.statModal":
                event.replyModal(getEchoStatEditModal(buildTracker.build,
                        details[1], Short.parseShort(details[2]))).queue();
                return;
            case "done":
                buildTracker.editableActionRow = new ActionRow[]{};
                // We overflow since now we just ensure the build card is updated
            case "main":
            default: // OH SHIT SOMETHING BROKE
                event.editComponents(buildTracker.editableActionRow).queue();
                new BuildCardUpdater(buildTracker).start();
        }
    }
    
    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        System.out.println(event.getComponentId());
        String[] details = event.getComponentId().split("\\$");
        BotCommandBuildTracker buildTracker = buildMap.get(details[1]);
        if (event.getMember() != null && event.getMember().getIdLong() != buildTracker.owner) {
            event.reply("This build card isn't yours, silly!").setEphemeral(true).queue();
            return;
        }
        switch (details[0]) {
            case "chara.chain":
                buildTracker.build.chainLength = Integer.parseInt(event.getValues().get(0));
                event.editComponents(getActionRowsForCharacter(buildTracker.build, details[1])).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            case "weap.rank":
                buildTracker.build.weaponRank = Integer.parseInt(event.getValues().get(0));
                event.editComponents(getActionRowsForWeapon(buildTracker.build, details[1])).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            case "chara.asc":
                if (Arrays.stream(Level.values()).filter(level ->
                        level.toString().contains(event.getValues().get(0))).anyMatch(level ->
                        buildTracker.build.characterLevel.toString().substring(1)
                                .equals(level.toString().substring(1))))
                    buildTracker.build.characterLevel = Level.valueOf(event.getValues().get(0)
                            + buildTracker.build.characterLevel.toString().substring(1));
                event.editComponents(getActionRowsForCharacter(buildTracker.build, details[1],
                        event.getValues().get(0).charAt(0))).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            case "weap.asc":
                if (Arrays.stream(Level.values()).filter(level ->
                        level.toString().contains(event.getValues().get(0))).anyMatch(level ->
                        buildTracker.build.weaponLevel.toString().substring(1)
                                .equals(level.toString().substring(1))))
                    buildTracker.build.weaponLevel = Level.valueOf(event.getValues().get(0)
                            + buildTracker.build.weaponLevel.toString().substring(1));
                event.editComponents(getActionRowsForWeapon(buildTracker.build, details[1],
                        event.getValues().get(0).charAt(0))).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            case "chara.level":
                buildTracker.build.characterLevel = Level.valueOf(event.getValues().get(0));
                event.editComponents(getActionRowsForCharacter(buildTracker.build, details[1])).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            case "weap.level":
                buildTracker.build.weaponLevel = Level.valueOf(event.getValues().get(0));
                event.editComponents(getActionRowsForWeapon(buildTracker.build, details[1])).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            case "echo.mainStat": {
                short echoID = Short.parseShort(details[2]);
                buildTracker.build.echoes[echoID].mainStat
                        = Stat.valueOf(event.getValues().get(0));
                event.editComponents(getActionRowsForEcho(buildTracker.build,
                        details[1], echoID)).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            }
            case "echo.stat2": {
                short echoID = Short.parseShort(details[2]);
                buildTracker.build.echoes[echoID].secondStat
                        = Stat.valueOf(event.getValues().get(0));
                event.editComponents(getActionRowsForEcho(buildTracker.build,
                        details[1], echoID)).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            }
            case "echo.subStat": {
                short echoID = Short.parseShort(details[2]);
                Echo echo = buildTracker.build.echoes[echoID];
                for (Stat stat : Stat.values()) // Just go through all of them
                    if (event.getValues().contains(stat.toString())) {
                        if (!echo.subStats.containsKey(stat))
                            echo.subStats.put(stat, 0.0f);
                    } else echo.subStats.remove(stat);
                event.editComponents(getActionRowsForEcho(buildTracker.build,
                        details[1], echoID)).queue();
                new BuildCardUpdater(buildTracker).start();
                return;
            }
            case "chara.rover":
                try {
                    String chara = event.getValues().get(0);
                    Character character = Character.getCharacterByName(chara);
                    if (character == null)
                        throw new NullPointerException();
                    buildTracker.build.character = character;
                    event.deferEdit().queue();
                    event.getHook().deleteOriginal().queue();
                    new BuildCardUpdater(buildTracker).start();
                } catch (Exception e) {
                    event.reply("Something went wrong.").setEphemeral(true).queue();
                }
                return;
        }
    }
    
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        System.out.println(event.getModalId());
        String[] details = event.getModalId().split("\\$");
        BotCommandBuildTracker buildTracker = buildMap.get(details[1]);
        if (event.getMember() != null && event.getMember().getIdLong() != buildTracker.owner) {
            event.reply("This build card isn't yours, silly!").setEphemeral(true).queue();
            return;
        }
        switch (details[0]) {
            case "skill.main": {
                String basic = event.getValue("basic").getAsString();
                String skill = event.getValue("skill").getAsString();
                String forte = event.getValue("forte").getAsString();
                String ult = event.getValue("ult").getAsString();
                String intro = event.getValue("intro").getAsString();
                try {
                    buildTracker.build.skillLevels[1] = Integer.parseInt(basic);
                    buildTracker.build.skillLevels[2] = Integer.parseInt(skill);
                    buildTracker.build.skillLevels[0] = Integer.parseInt(forte);
                    buildTracker.build.skillLevels[3] = Integer.parseInt(ult);
                    buildTracker.build.skillLevels[4] = Integer.parseInt(intro);
                    event.deferEdit().queue();
                    new BuildCardUpdater(buildTracker).start();
                } catch (NumberFormatException e) {
                    event.reply("You can only input integers, silly!").setEphemeral(true).queue();
                } catch (Exception e) {
                    event.reply("Something went wrong.").setEphemeral(true).queue();
                }
            }
            case "chara.main": {
                try {
                    String chara = event.getValue("chara").getAsString().trim();
                    if (chara.toLowerCase().contains("rover")) {
                        ArrayList<SelectOption> rovers = new ArrayList<>();
                        for (Element element : Element.values()) {
                            if (element == Element.Glacio) continue;
                            if (element == Element.Electro) continue;
                            if (element == Element.Fusion) continue;
                            rovers.add(SelectOption.of("Female " + element + " Rover",
                                    "FRover: " + element));
                            rovers.add(SelectOption.of("Male " + element + " Rover",
                                    "MRover: " + element));
                        }
                        event.reply("Which Rover are you referring to?")
                                .setEphemeral(true).addComponents(ActionRow.of(
                                        StringSelectMenu.create("chara.rover$" + details[1])
                                                .addOptions(rovers).build())).queue();
                    } else {
                        Character character = Character.getCharacterByName(chara);
                        if (character == null) {
                            event.reply("Make sure you've spelled the character's name properly.")
                                    .setEphemeral(true).queue();
                            return;
                        }
                        buildTracker.build.character = character;
                        event.deferEdit().queue();
                        new BuildCardUpdater(buildTracker).start();
                    }
                    return;
                } catch (Exception e) {
                    event.reply("Something went wrong.").setEphemeral(true).queue();
                    return;
                }
            }
            case "weap.main": {
                try {
                    String weap = event.getValue("weap").getAsString().trim();
                    Weapon weapon = Weapon.getWeaponByName(weap);
                    if (weapon == null) {
                        event.reply("Make sure you've spelled the weapon name properly.")
                                .setEphemeral(true).queue();
                        return;
                    }
                    buildTracker.build.weapon = weapon;
                    event.deferEdit().queue();
                    new BuildCardUpdater(buildTracker).start();
                    return;
                } catch (Exception e) {
                    event.reply("Something went wrong.").setEphemeral(true).queue();
                    return;
                }
            }
            case "echo.stat": {
                try {
                    Echo echo = buildTracker.build.echoes[Short.parseShort(details[2])];
                    /*
                    echo.mainStatMagnitude = Float.parseFloat(event.getValue("mainStat").getAsString());
                    echo.secondStatMagnitude = Float.parseFloat(event.getValue("secondStat").getAsString()); */
                    for (Stat stat : echo.subStats.keySet()) {
                        echo.subStats.put(stat, Float.parseFloat(event
                                .getValue("substat$" + stat.toString()).getAsString()));
                    }
                    event.deferEdit().queue();
                    new BuildCardUpdater(buildTracker).start();
                    return;
                } catch (Exception e) {
                    event.reply("Something went wrong.").setEphemeral(true).queue();
                    return;
                }
            }
            default:
                event.reply("Idk how you did it but something died").setEphemeral(true).queue();
        }
    }
}
