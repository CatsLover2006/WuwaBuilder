package ca.litten.discordbot.wuwabuilder.parser;

import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.wuwa.*;
import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@Order(2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuildParserTest {
    @BeforeAll
    public static void Startup() {
        HakushinInterface.init(); // We need the images
    }
    
    Build phrolova;
    
    @Test
    @Order(0)
    public void PhrolovaTest() throws IOException {
        phrolova = BuildParser.parseBuild(ImageIO.read(new File("testData/phrolova.JPEG")));
        // Character, weapon, levels, and dupes
        assertEquals(Character.getCharacterByID(1608), phrolova.character);
        assertSame(Level.f80, phrolova.characterLevel);
        assertEquals(0, phrolova.chainLength);
        assertEquals(Weapon.getWeaponByID(21050066), phrolova.weapon);
        assertEquals(Level.f80, phrolova.weaponLevel);
        assertEquals(1, phrolova.weaponRank);
        // Skills
        assertEquals(3, phrolova.skillLevels[0]);
        assertEquals(5, phrolova.skillLevels[1]);
        assertEquals(3, phrolova.skillLevels[2]);
        assertEquals(8, phrolova.skillLevels[3]);
        assertEquals(3, phrolova.skillLevels[4]);
        // Echo 1
        assertEquals(6000115, phrolova.echoes[0].echoID);
        assertEquals(19, phrolova.echoes[0].sonataID);
        assertEquals(Stat.flatATK, phrolova.echoes[0].mainStat);
        assertEquals(150, phrolova.echoes[0].mainStatMagnitude);
        assertEquals(Stat.critDMG, phrolova.echoes[0].secondStat);
        assertEquals(44, phrolova.echoes[0].secondStatMagnitude);
        assertTrue(phrolova.echoes[0].subStats.containsKey(Stat.skillBonus));
        assertTrue(phrolova.echoes[0].subStats.containsKey(Stat.critRate));
        assertTrue(phrolova.echoes[0].subStats.containsKey(Stat.energyRegen));
        assertTrue(phrolova.echoes[0].subStats.containsKey(Stat.flatATK));
        assertTrue(phrolova.echoes[0].subStats.containsKey(Stat.percentHP));
        assertEquals(9.4f, phrolova.echoes[0].subStats.get(Stat.skillBonus));
        assertEquals(9.3f, phrolova.echoes[0].subStats.get(Stat.critRate));
        assertEquals(9.2f, phrolova.echoes[0].subStats.get(Stat.energyRegen));
        assertEquals(40, phrolova.echoes[0].subStats.get(Stat.flatATK));
        assertEquals(8.6f, phrolova.echoes[0].subStats.get(Stat.percentHP));
        // Echo 2
        assertEquals(6000073, phrolova.echoes[1].echoID);
        assertEquals(12, phrolova.echoes[1].sonataID);
        assertEquals(Stat.flatATK, phrolova.echoes[1].mainStat);
        assertEquals(100, phrolova.echoes[1].mainStatMagnitude);
        assertEquals(Stat.havocBonus, phrolova.echoes[1].secondStat);
        assertEquals(30, phrolova.echoes[1].secondStatMagnitude);
        assertTrue(phrolova.echoes[1].subStats.containsKey(Stat.critRate));
        assertTrue(phrolova.echoes[1].subStats.containsKey(Stat.ultBonus));
        assertTrue(phrolova.echoes[1].subStats.containsKey(Stat.percentATK));
        assertTrue(phrolova.echoes[1].subStats.containsKey(Stat.skillBonus));
        assertTrue(phrolova.echoes[1].subStats.containsKey(Stat.percentDEF));
        assertEquals(7.5f, phrolova.echoes[1].subStats.get(Stat.critRate));
        assertEquals(7.9f, phrolova.echoes[1].subStats.get(Stat.ultBonus));
        assertEquals(8.6f, phrolova.echoes[1].subStats.get(Stat.percentATK));
        assertEquals(7.9f, phrolova.echoes[1].subStats.get(Stat.skillBonus));
        assertEquals(10.9f, phrolova.echoes[1].subStats.get(Stat.percentDEF));
        // Echo 3
        assertEquals(6000081, phrolova.echoes[2].echoID);
        assertEquals(12, phrolova.echoes[2].sonataID);
        assertEquals(Stat.flatATK, phrolova.echoes[2].mainStat);
        assertEquals(100, phrolova.echoes[2].mainStatMagnitude);
        assertEquals(Stat.havocBonus, phrolova.echoes[2].secondStat);
        assertEquals(30, phrolova.echoes[2].secondStatMagnitude);
        assertTrue(phrolova.echoes[2].subStats.containsKey(Stat.critRate));
        assertTrue(phrolova.echoes[2].subStats.containsKey(Stat.skillBonus));
        assertTrue(phrolova.echoes[2].subStats.containsKey(Stat.percentATK));
        assertTrue(phrolova.echoes[2].subStats.containsKey(Stat.critDMG));
        assertTrue(phrolova.echoes[2].subStats.containsKey(Stat.flatHP));
        assertEquals(6.3f, phrolova.echoes[2].subStats.get(Stat.critRate));
        assertEquals(8.6f, phrolova.echoes[2].subStats.get(Stat.skillBonus));
        assertEquals(11.6f, phrolova.echoes[2].subStats.get(Stat.percentATK));
        assertEquals(470, phrolova.echoes[2].subStats.get(Stat.flatHP));
        assertEquals(13.8f, phrolova.echoes[2].subStats.get(Stat.critDMG));
        // Echo 4
        assertEquals(6000118, phrolova.echoes[3].echoID);
        assertEquals(19, phrolova.echoes[3].sonataID);
        assertEquals(Stat.flatHP, phrolova.echoes[3].mainStat);
        assertEquals(2280, phrolova.echoes[3].mainStatMagnitude);
        assertEquals(Stat.percentATK, phrolova.echoes[3].secondStat);
        assertEquals(18, phrolova.echoes[3].secondStatMagnitude);
        assertTrue(phrolova.echoes[3].subStats.containsKey(Stat.critDMG));
        assertTrue(phrolova.echoes[3].subStats.containsKey(Stat.critRate));
        assertTrue(phrolova.echoes[3].subStats.containsKey(Stat.skillBonus));
        assertTrue(phrolova.echoes[3].subStats.containsKey(Stat.flatATK));
        assertTrue(phrolova.echoes[3].subStats.containsKey(Stat.percentHP));
        assertEquals(12.6f, phrolova.echoes[3].subStats.get(Stat.critDMG));
        assertEquals(7.5f, phrolova.echoes[3].subStats.get(Stat.critRate));
        assertEquals(10.1f, phrolova.echoes[3].subStats.get(Stat.skillBonus));
        assertEquals(30, phrolova.echoes[3].subStats.get(Stat.flatATK));
        assertEquals(8.6f, phrolova.echoes[3].subStats.get(Stat.percentHP));
        // Echo 5
        assertEquals(6000117, phrolova.echoes[4].echoID);
        assertEquals(19, phrolova.echoes[4].sonataID);
        assertEquals(Stat.flatHP, phrolova.echoes[4].mainStat);
        assertEquals(2280, phrolova.echoes[4].mainStatMagnitude);
        assertEquals(Stat.percentATK, phrolova.echoes[4].secondStat);
        assertEquals(18, phrolova.echoes[4].secondStatMagnitude);
        assertTrue(phrolova.echoes[4].subStats.containsKey(Stat.critDMG));
        assertTrue(phrolova.echoes[4].subStats.containsKey(Stat.critRate));
        assertTrue(phrolova.echoes[4].subStats.containsKey(Stat.skillBonus));
        assertTrue(phrolova.echoes[4].subStats.containsKey(Stat.percentATK));
        assertTrue(phrolova.echoes[4].subStats.containsKey(Stat.percentDEF));
        assertEquals(21, phrolova.echoes[4].subStats.get(Stat.critDMG));
        assertEquals(6.9f, phrolova.echoes[4].subStats.get(Stat.critRate));
        assertEquals(8.6f, phrolova.echoes[4].subStats.get(Stat.skillBonus));
        assertEquals(8.6f, phrolova.echoes[4].subStats.get(Stat.percentATK));
        assertEquals(8.1f, phrolova.echoes[4].subStats.get(Stat.percentDEF));
    }
    
    @Test
    @Order(2)
    public void ZaniTest() throws IOException {
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/zani.JPEG")));
        // Character, weapon, levels, and dupes
        assertEquals(Character.getCharacterByID(1507), build.character);
        assertSame(Level.e70, build.characterLevel);
        assertEquals(0, build.chainLength);
        assertEquals(Weapon.getWeaponByID(21040064), build.weapon);
        assertEquals(Level.a1, build.weaponLevel);
        assertEquals(1, build.weaponRank);
        // Skills
        assertEquals(5, build.skillLevels[0]);
        assertEquals(3, build.skillLevels[1]);
        assertEquals(3, build.skillLevels[2]);
        assertEquals(5, build.skillLevels[3]);
        assertEquals(3, build.skillLevels[4]);
        // Echo 1
        assertEquals(6000104, build.echoes[0].echoID);
        assertEquals(11, build.echoes[0].sonataID);
        assertEquals(Stat.flatATK, build.echoes[0].mainStat);
        assertEquals(100, build.echoes[0].mainStatMagnitude);
        assertEquals(Stat.spectroBonus, build.echoes[0].secondStat);
        assertEquals(30, build.echoes[0].secondStatMagnitude);
        assertTrue(build.echoes[0].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.percentATK));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.energyRegen));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.flatATK));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.flatDEF));
        assertEquals(15, build.echoes[0].subStats.get(Stat.critDMG));
        assertEquals(7.1f, build.echoes[0].subStats.get(Stat.percentATK));
        assertEquals(8.4f, build.echoes[0].subStats.get(Stat.energyRegen));
        assertEquals(60, build.echoes[0].subStats.get(Stat.flatDEF));
        assertEquals(40, build.echoes[0].subStats.get(Stat.flatATK));
        // Echo 2
        assertEquals(6000092, build.echoes[1].echoID);
        assertEquals(11, build.echoes[1].sonataID);
        assertEquals(Stat.flatATK, build.echoes[1].mainStat);
        assertEquals(150, build.echoes[1].mainStatMagnitude);
        assertEquals(Stat.critRate, build.echoes[1].secondStat);
        assertEquals(22, build.echoes[1].secondStatMagnitude);
        assertTrue(build.echoes[1].subStats.containsKey(Stat.critRate));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.percentHP));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.skillBonus));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.basicBonus));
        assertEquals(6.3f, build.echoes[1].subStats.get(Stat.critRate));
        assertEquals(12.6f, build.echoes[1].subStats.get(Stat.critDMG));
        assertEquals(7.9f, build.echoes[1].subStats.get(Stat.percentHP));
        assertEquals(7.9f, build.echoes[1].subStats.get(Stat.basicBonus));
        assertEquals(10.1f, build.echoes[1].subStats.get(Stat.skillBonus));
        // Echo 3
        assertEquals(6000074, build.echoes[2].echoID);
        assertEquals(11, build.echoes[2].sonataID);
        assertEquals(Stat.flatATK, build.echoes[2].mainStat);
        assertEquals(100, build.echoes[2].mainStatMagnitude);
        assertEquals(Stat.spectroBonus, build.echoes[2].secondStat);
        assertEquals(30, build.echoes[2].secondStatMagnitude);
        assertTrue(build.echoes[2].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.energyRegen));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.flatDEF));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.percentDEF));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.percentATK));
        assertEquals(16.2f, build.echoes[2].subStats.get(Stat.critDMG));
        assertEquals(10, build.echoes[2].subStats.get(Stat.energyRegen));
        assertEquals(8.6f, build.echoes[2].subStats.get(Stat.percentATK));
        assertEquals(10.9f, build.echoes[2].subStats.get(Stat.percentDEF));
        assertEquals(50, build.echoes[2].subStats.get(Stat.flatDEF));
        // Echo 4
        assertEquals(6000099, build.echoes[3].echoID);
        assertEquals(11, build.echoes[3].sonataID);
        assertEquals(Stat.flatHP, build.echoes[3].mainStat);
        assertEquals(2280, build.echoes[3].mainStatMagnitude);
        assertEquals(Stat.percentATK, build.echoes[3].secondStat);
        assertEquals(18, build.echoes[3].secondStatMagnitude);
        assertTrue(build.echoes[3].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.energyRegen));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.ultBonus));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.basicBonus));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.heavyBonus));
        assertEquals(12.6f, build.echoes[3].subStats.get(Stat.critDMG));
        assertEquals(8.4f, build.echoes[3].subStats.get(Stat.energyRegen));
        assertEquals(10.9f, build.echoes[3].subStats.get(Stat.ultBonus));
        assertEquals(11.6f, build.echoes[3].subStats.get(Stat.basicBonus));
        assertEquals(7.9f, build.echoes[3].subStats.get(Stat.heavyBonus));
        // Echo 5
        assertEquals(6000067, build.echoes[4].echoID);
        assertEquals(11, build.echoes[4].sonataID);
        assertEquals(Stat.flatHP, build.echoes[4].mainStat);
        assertEquals(2280, build.echoes[4].mainStatMagnitude);
        assertEquals(Stat.percentATK, build.echoes[4].secondStat);
        assertEquals(18, build.echoes[4].secondStatMagnitude);
        assertTrue(build.echoes[4].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.critRate));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.ultBonus));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.basicBonus));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.flatATK));
        assertEquals(12.6f, build.echoes[4].subStats.get(Stat.critDMG));
        assertEquals(7.5f, build.echoes[4].subStats.get(Stat.critRate));
        assertEquals(7.1f, build.echoes[4].subStats.get(Stat.ultBonus));
        assertEquals(8.6f, build.echoes[4].subStats.get(Stat.basicBonus));
        assertEquals(50, build.echoes[4].subStats.get(Stat.flatATK));
    }
    
    @Test
    @Order(1)
    public void RecognitionTest() throws IOException {
        Build fspectrover = BuildParser.parseBuild(ImageIO.read(new File("testData/spectrover.JPEG")));
        Build sanhua = BuildParser.parseBuild(ImageIO.read(new File("testData/sanhua.JPEG")));
        Build verina = BuildParser.parseBuild(ImageIO.read(new File("testData/verina.JPEG")));
        assertEquals(Character.getCharacterByID(1502), fspectrover.character);
        assertEquals(Character.getCharacterByID(1102), sanhua.character);
        assertEquals(Character.getCharacterByID(1503), verina.character);
        assertEquals(Weapon.getWeaponByID(21020064), fspectrover.weapon);
        assertEquals(Weapon.getWeaponByID(21020015), sanhua.weapon);
        assertEquals(Weapon.getWeaponByID(21050024), verina.weapon);
        assertEquals(3, fspectrover.chainLength);
        assertEquals(6, sanhua.chainLength);
        assertEquals(1, verina.chainLength);
    }
    
    @Test
    @Order(3)
    public void StatPageTest() throws IOException {
        // Compensate for my shit build
        phrolova = BuildParser.parseBuild(ImageIO.read(new File("testData/phrolova.JPEG")));
        phrolova.minorSkills[1] = false;
        phrolova.minorSkills[3] = false;
        phrolova.minorSkills[5] = false;
        phrolova.minorSkills[7] = false;
        StatPage statPage = StatPage.calculateStats(phrolova);
        assertTrue(Math.abs(statPage.HP - 16130) < 1, String.format("%f !≈ 16130", statPage.HP));
        assertTrue(Math.abs(statPage.ATK - 2053) < 1, String.format("%f !≈ 2053", statPage.ATK));
        assertTrue(Math.abs(statPage.DEF - 1188) < 1, String.format("%f !≈ 1188", statPage.DEF));
        assertTrue(Math.abs(statPage.critDMG - 241.4) < 0.1, String.format("%f !≈ 241.4", statPage.critDMG));
        assertTrue(Math.abs(statPage.critRate - 67) < 0.1, String.format("%f !≈ 67", statPage.critRate));
        assertTrue(Math.abs(statPage.energyRegen - 109.2) < 0.1, String.format("%f !≈ 109.2", statPage.energyRegen));
        assertTrue(Math.abs(statPage.skillBonus - 44.6) < 0.1, String.format("%f !≈ 44.6", statPage.skillBonus));
        assertTrue(Math.abs(statPage.ultBonus - 7.9) < 0.1, String.format("%f !≈ 7.9", statPage.ultBonus));
        assertTrue(Math.abs(statPage.havocBonus - 70) < 0.1, String.format("%f !≈ 70", statPage.havocBonus));
        assertEquals(0, statPage.basicBonus);
        assertEquals(0, statPage.heavyBonus);
        assertEquals(0, statPage.glacioBonus);
        assertEquals(0, statPage.fusionBonus);
        assertEquals(0, statPage.electroBonus);
        assertEquals(0, statPage.aeroBonus);
        assertEquals(0, statPage.spectroBonus);
        assertEquals(0, statPage.healingBonus);
    }
}
