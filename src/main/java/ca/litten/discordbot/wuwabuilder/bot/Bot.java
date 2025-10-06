package ca.litten.discordbot.wuwabuilder.bot;

import ca.litten.discordbot.wuwabuilder.CardBuilder;
import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.Stat;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.modals.Modal;
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
    
    protected static ActionRow[] getActionRowsForSkills (Build build, String identifier) {
        ActionRow[] actionRows = new ActionRow[3];
        for (int row = 0; row < 2; row++) {
            ArrayList<Button> buttons = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Button button;
                int idx = 1 + i * 2 - row;
                if (build.minorSkills[idx])
                    button = Button.primary("skill.minor.edit$" + identifier + "$" + idx,
                            generateMinorSkillText(build.character.getStatBuf(idx).stat.stat));
                else
                    button = Button.secondary("skill.minor.edit$" + identifier + "$" + idx,
                            generateMinorSkillText(build.character.getStatBuf(idx).stat.stat));
                buttons.add(button);
            }
            if (build.asensionPassive > 1 - row)
                buttons.add(2, Button.primary("skill.minor.edit$" + identifier + "$a" + (2 - row),
                        build.character.getSkillName(7 - row)));
            else
                buttons.add(2, Button.secondary("skill.minor.edit$" + identifier + "$a" + (2 - row),
                        build.character.getSkillName(7 - row)));
            actionRows[row] = ActionRow.of(buttons);
        }
        actionRows[2] = ActionRow.of(Button.primary("skill.modal" + identifier, "Edit Forte Levels"),
                Button.success("main$" + identifier, "Back"));
        return actionRows;
    }
    
    protected static ActionRow[] getActionRowsForCharacter (Build build, String identifier) {
        return getActionRowsForCharacter(build, identifier, build.characterLevel.toString().charAt(0));
    }
    
    protected static ActionRow[] getActionRowsForCharacter (Build build, String identifier, char charA) {
        ActionRow[] actionRows = new ActionRow[4];
        SelectOption[] charaAscensionOptions = new SelectOption[7];
        charaAscensionOptions[0] = SelectOption.of("Ascension 0", "a")
                .withDescription("Max Level: 20").withDefault(charA == 'a');
        charaAscensionOptions[1] = SelectOption.of("Ascension 1", "b")
                .withDescription("Max Level: 40").withDefault(charA == 'b');
        charaAscensionOptions[2] = SelectOption.of("Ascension 2", "c")
                .withDescription("Max Level: 50").withDefault(charA == 'c');
        charaAscensionOptions[3] = SelectOption.of("Ascension 3", "d")
                .withDescription("Max Level: 60").withDefault(charA == 'd');
        charaAscensionOptions[4] = SelectOption.of("Ascension 4", "e")
                .withDescription("Max Level: 70").withDefault(charA == 'e');
        charaAscensionOptions[5] = SelectOption.of("Ascension 5", "f")
                .withDescription("Max Level: 80").withDefault(charA == 'f');
        charaAscensionOptions[6] = SelectOption.of("Ascension 6", "g")
                .withDescription("Max Level: 90").withDefault(charA == 'g');
        actionRows[0] = ActionRow.of(StringSelectMenu.create("chara.asc$" + identifier)
                .addOptions(charaAscensionOptions).build());
        ArrayList<SelectOption> charaLevelOptions = new ArrayList<>();
        int charaLevel = Integer.parseInt(build.characterLevel.toString().substring(1));
        switch (charA) {
            case 'a':
                for (int i = 1; i <= 20; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "a" + i)
                            .withDescription("Level " + i + "/20").withDefault(charaLevel == i));
                break;
            case 'b':
                for (int i = 20; i <= 40; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "b" + i)
                            .withDescription("Level " + i + "/40").withDefault(charaLevel == i));
                break;
            case 'c':
                for (int i = 40; i <= 50; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "c" + i)
                            .withDescription("Level " + i + "/50").withDefault(charaLevel == i));
                break;
            case 'd':
                for (int i = 50; i <= 60; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "d" + i)
                            .withDescription("Level " + i + "/60").withDefault(charaLevel == i));
                break;
            case 'e':
                for (int i = 60; i <= 70; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "e" + i)
                            .withDescription("Level " + i + "/70").withDefault(charaLevel == i));
                break;
            case 'f':
                for (int i = 70; i <= 80; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "f" + i)
                            .withDescription("Level " + i + "/80").withDefault(charaLevel == i));
                break;
            case 'g':
                for (int i = 80; i <= 90; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "g" + i)
                            .withDescription("Level " + i + "/90").withDefault(charaLevel == i));
                break;
        }
        actionRows[1] = ActionRow.of(StringSelectMenu.create("chara.level$" + identifier)
                .addOptions(charaLevelOptions).build());
        SelectOption[] selectOptions = new SelectOption[7];
        selectOptions[0] = SelectOption.of("Resonance Chain 0", "0")
                .withDefault(build.chainLength == 0);
        for (int i = 0; i < 6; i++) {
            selectOptions[i + 1] = SelectOption.of("Resonance Chain " + (i + 1), String.valueOf(i + 1))
                    .withDescription(build.character.getChainName(i))
                    .withDefault(build.chainLength == i + 1);
        }
        actionRows[2] = ActionRow.of(StringSelectMenu.create("chara.chain$" + identifier)
                .addOptions(selectOptions).build());
        actionRows[3] = ActionRow.of(
                Button.primary("chara.modal$" + identifier, "Change Resonator"),
                Button.success("main$" + identifier, "Back"));
        return actionRows;
    }
    
    protected static ActionRow[] getActionRowsForWeapon (Build build, String identifier) {
        return getActionRowsForWeapon(build, identifier, build.weaponLevel.toString().charAt(0));
    }
    
    protected static ActionRow[] getActionRowsForWeapon (Build build, String identifier, char charA) {
        ActionRow[] actionRows = new ActionRow[4];
        SelectOption[] charaAscensionOptions = new SelectOption[7];
        charaAscensionOptions[0] = SelectOption.of("Ascension 0", "a")
                .withDescription("Max Level: 20").withDefault(charA == 'a');
        charaAscensionOptions[1] = SelectOption.of("Ascension 1", "b")
                .withDescription("Max Level: 40").withDefault(charA == 'b');
        charaAscensionOptions[2] = SelectOption.of("Ascension 2", "c")
                .withDescription("Max Level: 50").withDefault(charA == 'c');
        charaAscensionOptions[3] = SelectOption.of("Ascension 3", "d")
                .withDescription("Max Level: 60").withDefault(charA == 'd');
        charaAscensionOptions[4] = SelectOption.of("Ascension 4", "e")
                .withDescription("Max Level: 70").withDefault(charA == 'e');
        charaAscensionOptions[5] = SelectOption.of("Ascension 5", "f")
                .withDescription("Max Level: 80").withDefault(charA == 'f');
        charaAscensionOptions[6] = SelectOption.of("Ascension 6", "g")
                .withDescription("Max Level: 90").withDefault(charA == 'g');
        actionRows[0] = ActionRow.of(StringSelectMenu.create("weap.asc$" + identifier)
                .addOptions(charaAscensionOptions).build());
        ArrayList<SelectOption> charaLevelOptions = new ArrayList<>();
        int charaLevel = Integer.parseInt(build.weaponLevel.toString().substring(1));
        switch (charA) {
            case 'a':
                for (int i = 1; i <= 20; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "a" + i)
                            .withDescription("Level " + i + "/20").withDefault(charaLevel == i));
                break;
            case 'b':
                for (int i = 20; i <= 40; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "b" + i)
                            .withDescription("Level " + i + "/40").withDefault(charaLevel == i));
                break;
            case 'c':
                for (int i = 40; i <= 50; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "c" + i)
                            .withDescription("Level " + i + "/50").withDefault(charaLevel == i));
                break;
            case 'd':
                for (int i = 50; i <= 60; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "d" + i)
                            .withDescription("Level " + i + "/60").withDefault(charaLevel == i));
                break;
            case 'e':
                for (int i = 60; i <= 70; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "e" + i)
                            .withDescription("Level " + i + "/70").withDefault(charaLevel == i));
                break;
            case 'f':
                for (int i = 70; i <= 80; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "f" + i)
                            .withDescription("Level " + i + "/80").withDefault(charaLevel == i));
                break;
            case 'g':
                for (int i = 80; i <= 90; i++)
                    charaLevelOptions.add(SelectOption.of("Level " + i, "g" + i)
                            .withDescription("Level " + i + "/90").withDefault(charaLevel == i));
                break;
        }
        actionRows[1] = ActionRow.of(StringSelectMenu.create("weap.level$" + identifier)
                .addOptions(charaLevelOptions).build());
        SelectOption[] selectOptions = new SelectOption[5];
        for (int i = 0; i < 5; i++) {
            selectOptions[i] = SelectOption.of("Rank " + (i + 1), String.valueOf(i))
                    .withDefault(build.weaponRank == i);
        }
        actionRows[2] = ActionRow.of(StringSelectMenu.create("weap.rank$" + identifier)
                .addOptions(selectOptions).build());
        actionRows[3] = ActionRow.of(
                Button.primary("weap.modal$" + identifier, "Change Weapon"),
                Button.success("main$" + identifier, "Back"));
        return actionRows;
    }
    
    protected static Modal getForteEditModal(Build build, String identifier) {
        TextInput basic = TextInput.create("basic", TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[1])).build();
        TextInput skill = TextInput.create("skill", TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[2])).build();
        TextInput forte = TextInput.create("forte", TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[0])).build();
        TextInput ult = TextInput.create("ult", TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[3])).build();
        TextInput intro = TextInput.create("intro", TextInputStyle.SHORT)
                .setPlaceholder("1 ~ 10").setRequiredRange(1, 2)
                .setValue(String.valueOf(build.skillLevels[4])).build();
        return Modal.create("skill.main$" + identifier, build.character.getName())
                .addComponents(
                        Label.of(build.character.getSkillName(1), basic),
                        Label.of(build.character.getSkillName(2), skill),
                        Label.of(build.character.getSkillName(0), forte),
                        Label.of(build.character.getSkillName(3), ult),
                        Label.of(build.character.getSkillName(4), intro)).build();
    }
    
    protected static Modal getCharacterEditModal(Build build, String identifier) {
        TextInput basic = TextInput.create("chara", TextInputStyle.SHORT)
                .setPlaceholder("Resonator Name").setRequiredRange(1, 100)
                .setValue(build.character.getName()).build();
        return Modal.create("chara.main$" + identifier, build.character.getName())
                .addComponents(Label.of("Resonator Name", basic)).build();
    }
    
    protected static Modal getWeaponEditModal(Build build, String identifier) {
        TextInput skill = TextInput.create("weap", TextInputStyle.SHORT)
                .setPlaceholder("Weapon Name").setRequiredRange(1, 100)
                .setValue(build.weapon.getName()).build();
        return Modal.create("weap.main$" + identifier, build.character.getName())
                .addComponents(Label.of("Weapon Name", skill)).build();
    }
}
