package ca.litten.discordbot.wuwabuilder.bot;

import ca.litten.discordbot.wuwabuilder.CardBuilder;
import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.Echo;
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
            default:
                return stat.string + "+";
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
                    button = Button.primary("skill.minor$" + identifier + "$" + idx,
                            generateMinorSkillText(build.character.getStatBuf(idx).stat.stat));
                else
                    button = Button.secondary("skill.minor$" + identifier + "$" + idx,
                            generateMinorSkillText(build.character.getStatBuf(idx).stat.stat));
                buttons.add(button);
            }
            if (build.asensionPassive > 1 - row)
                buttons.add(2, Button.primary("skill.minor$" + identifier + "$a" + (2 - row),
                        build.character.getSkillName(7 - row)));
            else
                buttons.add(2, Button.secondary("skill.minor$" + identifier + "$a" + (2 - row),
                        build.character.getSkillName(7 - row)));
            actionRows[row] = ActionRow.of(buttons);
        }
        actionRows[2] = ActionRow.of(Button.primary("skill.modal$" + identifier, "Edit Forte Levels"),
                Button.success("main$" + identifier, "Back"));
        return actionRows;
    }
    
    protected static ActionRow[] getActionRowsForEchoMenu (Build build, String identifier) {
        ActionRow[] actionRows = new ActionRow[2];
        actionRows[0] = ActionRow.of(Button.primary("echo.edit$" + identifier + "$0", "Edit Echo 1"),
                Button.primary("echo.edit$" + identifier + "$1", "Edit Echo 2"),
                Button.primary("echo.edit$" + identifier + "$2", "Edit Echo 3"),
                Button.primary("echo.edit$" + identifier + "$3", "Edit Echo 4"),
                Button.primary("echo.edit$" + identifier + "$4", "Edit Echo 5"));
        actionRows[1] = ActionRow.of(Button.success("main$" + identifier, "Back"));
        return actionRows;
    }
    
    protected static ActionRow[] getActionRowsForEcho(Build build, String identifier, short echoID) {
        ActionRow[] actionRows = new ActionRow[5];
        Echo echo = build.echoes[echoID];
        SelectOption[] mainStats = new SelectOption[2];
        mainStats[0] = SelectOption.of(Stat.flatATK.string, "flatATK")
                .withDefault(echo.mainStat == Stat.flatATK);
        mainStats[1] = SelectOption.of(Stat.flatHP.string, "flatHP")
                .withDefault(echo.mainStat == Stat.flatHP);
        actionRows[0] = ActionRow.of(StringSelectMenu.create("echo.mainStat$" + identifier + "$" + echoID)
                .addOptions(mainStats).build());
        SelectOption[] secondaryStats = new SelectOption[13];
        secondaryStats[0] = SelectOption.of(Stat.percentATK.string, "percentATK")
                .withDefault(echo.secondStat == Stat.percentATK);
        secondaryStats[1] = SelectOption.of(Stat.percentHP.string, "percentHP")
                .withDefault(echo.secondStat == Stat.percentHP);
        secondaryStats[2] = SelectOption.of(Stat.percentDEF.string, "percentDEF")
                .withDefault(echo.secondStat == Stat.percentDEF);
        secondaryStats[3] = SelectOption.of(Stat.critDMG.string, "critDMG")
                .withDefault(echo.secondStat == Stat.critDMG);
        secondaryStats[4] = SelectOption.of(Stat.critRate.string, "critRate")
                .withDefault(echo.secondStat == Stat.critRate);
        secondaryStats[5] = SelectOption.of(Stat.energyRegen.string, "energyRegen")
                .withDefault(echo.secondStat == Stat.energyRegen);
        secondaryStats[6] = SelectOption.of(Stat.aeroBonus.string, "aeroBonus")
                .withDefault(echo.secondStat == Stat.aeroBonus);
        secondaryStats[7] = SelectOption.of(Stat.glacioBonus.string, "glacioBonus")
                .withDefault(echo.secondStat == Stat.glacioBonus);
        secondaryStats[8] = SelectOption.of(Stat.fusionBonus.string, "fusionBonus")
                .withDefault(echo.secondStat == Stat.fusionBonus);
        secondaryStats[9] = SelectOption.of(Stat.electroBonus.string, "electroBonus")
                .withDefault(echo.secondStat == Stat.electroBonus);
        secondaryStats[10] = SelectOption.of(Stat.havocBonus.string, "havocBonus")
                .withDefault(echo.secondStat == Stat.havocBonus);
        secondaryStats[11] = SelectOption.of(Stat.spectroBonus.string, "spectroBonus")
                .withDefault(echo.secondStat == Stat.spectroBonus);
        secondaryStats[12] = SelectOption.of(Stat.healingBonus.string, "healingBonus")
                .withDefault(echo.secondStat == Stat.healingBonus);
        actionRows[1] = ActionRow.of(StringSelectMenu.create("echo.stat2$" + identifier + "$" + echoID)
                .addOptions(secondaryStats).build());
        SelectOption[] subStats = new SelectOption[13];
        subStats[0] = SelectOption.of(Stat.flatATK.string, "flatATK")
                .withDefault(echo.subStats.containsKey(Stat.flatATK));
        subStats[1] = SelectOption.of(Stat.flatHP.string, "flatHP")
                .withDefault(echo.subStats.containsKey(Stat.flatHP));
        subStats[2] = SelectOption.of(Stat.flatDEF.string, "flatDEF")
                .withDefault(echo.subStats.containsKey(Stat.flatDEF));
        subStats[3] = SelectOption.of(Stat.percentATK.string, "percentATK")
                .withDefault(echo.subStats.containsKey(Stat.percentATK));
        subStats[4] = SelectOption.of(Stat.percentHP.string, "percentHP")
                .withDefault(echo.subStats.containsKey(Stat.percentHP));
        subStats[5] = SelectOption.of(Stat.percentDEF.string, "percentDEF")
                .withDefault(echo.subStats.containsKey(Stat.percentDEF));
        subStats[6] = SelectOption.of(Stat.critDMG.string, "critDMG")
                .withDefault(echo.subStats.containsKey(Stat.critDMG));
        subStats[7] = SelectOption.of(Stat.critRate.string, "critRate")
                .withDefault(echo.subStats.containsKey(Stat.critRate));
        subStats[8] = SelectOption.of(Stat.energyRegen.string, "energyRegen")
                .withDefault(echo.subStats.containsKey(Stat.energyRegen));
        subStats[9] = SelectOption.of(Stat.basicBonus.string, "basicBonus")
                .withDefault(echo.subStats.containsKey(Stat.basicBonus));
        subStats[10] = SelectOption.of(Stat.heavyBonus.string, "heavyBonus")
                .withDefault(echo.subStats.containsKey(Stat.heavyBonus));
        subStats[11] = SelectOption.of(Stat.skillBonus.string, "skillBonus")
                .withDefault(echo.subStats.containsKey(Stat.skillBonus));
        subStats[12] = SelectOption.of(Stat.ultBonus.string, "ultBonus")
                .withDefault(echo.subStats.containsKey(Stat.ultBonus));
        actionRows[2] = ActionRow.of(StringSelectMenu.create("echo.subStat$" + identifier + "$" + echoID)
                .setMaxValues(5).addOptions(subStats).build());
        actionRows[3] = ActionRow.of(Button.danger("echo.edit$" + identifier + "$" + echoID + "$a", "Sonata Selector (TODO)"));
        actionRows[4] = ActionRow.of(Button.danger("echo.edit$" + identifier + "$" + echoID + "$b", "Edit Echo and Main Stat Values (TODO)"),
                Button.primary("echo.statModal$" + identifier + "$" + echoID, "Edit Substat Values"),
                Button.success("echo$" + identifier, "Back"));
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
    
    protected static Modal getEchoStatEditModal(Build build, String identifier, short echoID) {
        Echo echo = build.echoes[echoID];
        Label[] inputs = new Label[echo.subStats.size()];
        /* // TODO: MOVE THESE TO MAIN ECHO EDIT MODAL
        inputs[0] = Label.of(echo.mainStat.string,
                TextInput.create("mainStat", TextInputStyle.SHORT)
                .setValue(String.valueOf(build.echoes[echoID].mainStatMagnitude)).build());
        inputs[1] = Label.of(echo.secondStat.string,
                TextInput.create("secondStat", TextInputStyle.SHORT)
                        .setValue(String.valueOf(build.echoes[echoID].secondStatMagnitude)).build());*/
        int i = 0;
        for (Stat stat : Stat.values())
            if (echo.subStats.containsKey(stat)) {
                inputs[i] = Label.of(stat.string,
                        TextInput.create("substat$" + stat, TextInputStyle.SHORT)
                                .setValue(String.valueOf(build.echoes[echoID].subStats.get(stat))).build());
                i++;
            }
        return Modal.create("echo.stat$" + identifier + "$" + echoID, build.character.getName())
                .addComponents(inputs).build();
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
