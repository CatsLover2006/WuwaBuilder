package ca.litten.discordbot.wuwabuilder.bot;

import ca.litten.discordbot.wuwabuilder.CardBuilder;
import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.Stat;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
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
    
    public static void main(String[] args) throws Exception {
        JSONObject config = loadConfig();
        HakushinInterface.init(new URL(config.getString("hakushin")));
        cardBuilder = new CardBuilder(true);
        JDA jda = JDABuilder.createLight(config.getString("token"), Collections.emptyList())
                .addEventListeners(new GenerationCommandListener()).build();
        WebhookHandler webhookHandler = new WebhookHandler(jda,
                config.getString("appid"), config.getInt("webhookPort"),
                config.getString("pubKey"));
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                Commands.slash("generate", "Generate build")
                        .addOption(OptionType.ATTACHMENT, "image", "Official Bot Image", true)
        );
        commands.queue();
    }
    
    protected static class BotCommandBuildTracker {
        public Build build;
        public InteractionHook hook;
        public ActionRow[] editableActionRow;
        public long owner;
        
        public BotCommandBuildTracker(Build build, InteractionHook hook, ActionRow[] editableActionRow, long owner) {
            this.build = build;
            this.hook = hook;
            this.editableActionRow = editableActionRow;
            this.owner = owner;
        }
    }
    
    protected static void updateBuildCard(BotCommandBuildTracker buildTracker) throws IOException {
        BufferedImage card = cardBuilder.createCard(buildTracker.build);
        ByteArrayOutputStream cardBytes = new ByteArrayOutputStream();
        ImageIO.write(card, "png", cardBytes);
        String name = buildTracker.owner + "." + buildTracker.build.character.getId() + "." + Instant.now().getEpochSecond();
        buildTracker.hook.editOriginalAttachments(FileUpload.fromData(cardBytes.toByteArray(), name + ".png")).queue();
    }
    
    protected static String generateMinorSkillText(Stat stat) {
        switch (stat) {
            case flatATK:
            case percentATK:
                return "ATK+";
            case flatDEF:
            case percentDEF:
                return "DEF+";
            case flatHP:
            case percentHP:
                return "HP+";
            case critRate:
                return "Crit. Rate+";
            case critDMG:
                return "Crit. DMG+";
            case basicBonus:
                return "Basic Attack DMG Bonus+";
            case heavyBonus:
                return "Heavy Attack DMG Bonus+";
            case skillBonus:
                return "Resonance Skill DMG Bonus+";
            case ultBonus:
                return "Resonance Liberation DMG Bonus+";
            case healingBonus:
                return "Healing Bonus+";
            default:
                String elementName = stat.name().replace("Bonus", "");
                return elementName.substring(0, 1).toUpperCase() // Capitalize first letter
                        + elementName.substring(1).toLowerCase()
                        + " DMG Bonus+";
        }
    }
    
    protected static ActionRow[] getActionRowsForMinorSkills (Build build, String identifier) {
        ActionRow[] actionRows = new ActionRow[3];
        for (int row = 0; row < 2; row++) {
            ArrayList<Button> buttons = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Button button;
                int idx = 1 + i * 2 - row;
                if (build.minorSkills[idx])
                    button = Button.primary("edit.skill.minor.edit$" + identifier + "$" + idx,
                            generateMinorSkillText(build.character.getStatBuf(idx).stat.stat));
                else
                    button = Button.secondary("edit.skill.minor.edit$" + identifier + "$" + idx,
                            generateMinorSkillText(build.character.getStatBuf(idx).stat.stat));
                buttons.add(button);
            }
            if (build.asensionPassive > 1 - row)
                buttons.add(2, Button.primary("edit.skill.minor.edit$" + identifier + "$a" + (2 - row),
                        build.character.getSkillName(7 - row)));
            else
                buttons.add(2, Button.secondary("edit.skill.minor.edit$" + identifier + "$a" + (2 - row),
                        build.character.getSkillName(7 - row)));
            actionRows[row] = ActionRow.of(buttons);
        }
        actionRows[2] = ActionRow.of(Button.success("main$" + identifier, "Back"));
        return actionRows;
    }
    
    protected static ActionRow[] getActionRowsForChains (Build build, String identifier) {
        ActionRow[] actionRows = new ActionRow[3];
        SelectOption[] selectOptions = new SelectOption[7];
        selectOptions[0] = SelectOption.of("Resonance Chain 0", "0")
                .withDefault(build.chainLength == 0);
        for (int i = 0; i < 6; i++) {
            selectOptions[i + 1] = SelectOption.of("Resonance Chain " + (i + 1), String.valueOf(i + 1))
                    .withDescription(build.character.getChainName(i))
                    .withDefault(build.chainLength == i + 1);
        }
        actionRows[0] = ActionRow.of(StringSelectMenu.create("edit.chain.chain$" + identifier)
                .addOptions(selectOptions).build());
        selectOptions = new SelectOption[5];
        for (int i = 0; i < 5; i++) {
            selectOptions[i] = SelectOption.of("Rank " + (i + 1), String.valueOf(i))
                    .withDefault(build.weaponRank == i);
        }
        actionRows[1] = ActionRow.of(StringSelectMenu.create("edit.chain.rank$" + identifier)
                .addOptions(selectOptions).build());
        actionRows[2] = ActionRow.of(Button.success("main$" + identifier, "Back"));
        return actionRows;
    }
    
    protected static ActionRow[] getActionRowsForCharacterLevels(Build build, String identifier) {
        return getActionRowsForCharacterLevels(build, identifier,
                build.characterLevel.toString().charAt(0),
                build.weaponLevel.toString().charAt(0));
    }
    
    protected static ActionRow[] getActionRowsForCharacterLevels(Build build, String identifier, char charA, char weapA) {
        ActionRow[] actionRows = new ActionRow[5];
        SelectOption[] charaAscensionOptions = new SelectOption[7];
        charaAscensionOptions[0] = SelectOption.of("Character Ascension 0", "a")
                .withDescription("Max Level: 20").withDefault(charA == 'a');
        charaAscensionOptions[1] = SelectOption.of("Character Ascension 1", "b")
                .withDescription("Max Level: 40").withDefault(charA == 'b');
        charaAscensionOptions[2] = SelectOption.of("Character Ascension 2", "c")
                .withDescription("Max Level: 50").withDefault(charA == 'c');
        charaAscensionOptions[3] = SelectOption.of("Character Ascension 3", "d")
                .withDescription("Max Level: 60").withDefault(charA == 'd');
        charaAscensionOptions[4] = SelectOption.of("Character Ascension 4", "e")
                .withDescription("Max Level: 70").withDefault(charA == 'e');
        charaAscensionOptions[5] = SelectOption.of("Character Ascension 5", "f")
                .withDescription("Max Level: 80").withDefault(charA == 'f');
        charaAscensionOptions[6] = SelectOption.of("Character Ascension 6", "g")
                .withDescription("Max Level: 90").withDefault(charA == 'g');
        actionRows[0] = ActionRow.of(StringSelectMenu.create("edit.chara.level.chara.asc$" + identifier)
                        .addOptions(charaAscensionOptions).build());
        SelectOption[] weapAscensionOptions = new SelectOption[7];
        weapAscensionOptions[0] = SelectOption.of("Weapon Ascension 0", "a")
                .withDescription("Max Level: 20").withDefault(weapA == 'a');
        weapAscensionOptions[1] = SelectOption.of("Weapon Ascension 1", "b")
                .withDescription("Max Level: 40").withDefault(weapA == 'b');
        weapAscensionOptions[2] = SelectOption.of("Weapon Ascension 2", "c")
                .withDescription("Max Level: 50").withDefault(weapA == 'c');
        weapAscensionOptions[3] = SelectOption.of("Weapon Ascension 3", "d")
                .withDescription("Max Level: 60").withDefault(weapA == 'd');
        weapAscensionOptions[4] = SelectOption.of("Weapon Ascension 4", "e")
                .withDescription("Max Level: 70").withDefault(weapA == 'e');
        weapAscensionOptions[5] = SelectOption.of("Weapon Ascension 5", "f")
                .withDescription("Max Level: 80").withDefault(weapA == 'f');
        weapAscensionOptions[6] = SelectOption.of("Weapon Ascension 6", "g")
                .withDescription("Max Level: 90").withDefault(weapA == 'g');
        actionRows[2] = ActionRow.of(StringSelectMenu.create("edit.chara.level.weap.asc$" + identifier)
                .addOptions(weapAscensionOptions).build());
        ArrayList<SelectOption> charaLevelOptions = new ArrayList<>();
        int charaLevel = Integer.parseInt(build.characterLevel.toString().substring(1));
        switch (charA) {
            case 'a':
                for (int i = 1; i <= 20; i++)
                    charaLevelOptions.add(SelectOption.of("Character Level " + i, "a" + i)
                            .withDescription("Level " + i + "/20").withDefault(charaLevel == i));
                break;
            case 'b':
                for (int i = 20; i <= 40; i++)
                    charaLevelOptions.add(SelectOption.of("Character Level " + i, "b" + i)
                            .withDescription("Level " + i + "/40").withDefault(charaLevel == i));
                break;
            case 'c':
                for (int i = 40; i <= 50; i++)
                    charaLevelOptions.add(SelectOption.of("Character Level " + i, "c" + i)
                            .withDescription("Level " + i + "/50").withDefault(charaLevel == i));
                break;
            case 'd':
                for (int i = 50; i <= 60; i++)
                    charaLevelOptions.add(SelectOption.of("Character Level " + i, "d" + i)
                            .withDescription("Level " + i + "/60").withDefault(charaLevel == i));
                break;
            case 'e':
                for (int i = 60; i <= 70; i++)
                    charaLevelOptions.add(SelectOption.of("Character Level " + i, "e" + i)
                            .withDescription("Level " + i + "/70").withDefault(charaLevel == i));
                break;
            case 'f':
                for (int i = 70; i <= 80; i++)
                    charaLevelOptions.add(SelectOption.of("Character Level " + i, "f" + i)
                            .withDescription("Level " + i + "/80").withDefault(charaLevel == i));
                break;
            case 'g':
                for (int i = 80; i <= 90; i++)
                    charaLevelOptions.add(SelectOption.of("Character Level " + i, "g" + i)
                            .withDescription("Level " + i + "/90").withDefault(charaLevel == i));
                break;
        }
        actionRows[1] = ActionRow.of(StringSelectMenu.create("edit.chara.level.chara$" + identifier)
                .addOptions(charaLevelOptions).build());
        charaLevelOptions = new ArrayList<>();
        charaLevel = Integer.parseInt(build.weaponLevel.toString().substring(1));
        switch (weapA) {
            case 'a':
                for (int i = 1; i <= 20; i++)
                    charaLevelOptions.add(SelectOption.of("Weapon Level " + i, "a" + i)
                            .withDescription("Level " + i + "/20").withDefault(charaLevel == i));
                break;
            case 'b':
                for (int i = 20; i <= 40; i++)
                    charaLevelOptions.add(SelectOption.of("Weapon Level " + i, "b" + i)
                            .withDescription("Level " + i + "/40").withDefault(charaLevel == i));
                break;
            case 'c':
                for (int i = 40; i <= 50; i++)
                    charaLevelOptions.add(SelectOption.of("Weapon Level " + i, "c" + i)
                            .withDescription("Level " + i + "/50").withDefault(charaLevel == i));
                break;
            case 'd':
                for (int i = 50; i <= 60; i++)
                    charaLevelOptions.add(SelectOption.of("Weapon Level " + i, "d" + i)
                            .withDescription("Level " + i + "/60").withDefault(charaLevel == i));
                break;
            case 'e':
                for (int i = 60; i <= 70; i++)
                    charaLevelOptions.add(SelectOption.of("Weapon Level " + i, "e" + i)
                            .withDescription("Level " + i + "/70").withDefault(charaLevel == i));
                break;
            case 'f':
                for (int i = 70; i <= 80; i++)
                    charaLevelOptions.add(SelectOption.of("Weapon Level " + i, "f" + i)
                            .withDescription("Level " + i + "/80").withDefault(charaLevel == i));
                break;
            case 'g':
                for (int i = 80; i <= 90; i++)
                    charaLevelOptions.add(SelectOption.of("Weapon Level " + i, "g" + i)
                            .withDescription("Level " + i + "/90").withDefault(charaLevel == i));
                break;
        }
        actionRows[3] = ActionRow.of(StringSelectMenu.create("edit.chara.level.weap$" + identifier)
                .addOptions(charaLevelOptions).build());
        actionRows[4] = ActionRow.of(Button.success("main$" + identifier, "Back"),
                Button.primary("edit.chara.main$" + identifier, "Change Character or Weapon"));
        return actionRows;
    }
    
    protected static Modal getForteEditModal(Build build, String identifier) {
        TextInput basic = TextInput.create("basic",
                        build.character.getSkillName(1), TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[1])).build();
        TextInput skill = TextInput.create("skill",
                        build.character.getSkillName(2), TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[2])).build();
        TextInput forte = TextInput.create("forte",
                        build.character.getSkillName(0), TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[0])).build();
        TextInput ult = TextInput.create("ult",
                        build.character.getSkillName(3), TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[3])).build();
        TextInput intro = TextInput.create("intro",
                        build.character.getSkillName(4), TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[4])).build();
        return Modal.create("edit.skill.main$" + identifier, build.character.getName())
                .addComponents(ActionRow.of(basic), ActionRow.of(skill), ActionRow.of(forte),
                        ActionRow.of(ult), ActionRow.of(intro)).build();
    }
    
    protected static Modal getCharacterEditModal(Build build, String identifier) {
        TextInput basic = TextInput.create("chara",
                        build.character.getSkillName(1), TextInputStyle.SHORT)
                .setPlaceholder("Character Name").setRequiredRange(1, 100)
                .setValue(build.character.getName()).build();
        TextInput skill = TextInput.create("weap",
                        build.character.getSkillName(2), TextInputStyle.SHORT)
                .setPlaceholder("Weapon Name").setRequiredRange(1, 100)
                .setValue(build.weapon.getName()).build();
        return Modal.create("edit.chara.main$" + identifier, build.character.getName())
                .addComponents(ActionRow.of(basic), ActionRow.of(skill)).build();
    }
}
