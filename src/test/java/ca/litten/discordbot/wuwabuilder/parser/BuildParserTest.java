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
public class BuildParserTest {
    @BeforeAll
    public static void Startup() {
        HakushinInterface.init(); // We need the images
    }
    
    @Test
    public void PhrolovaTest() throws IOException {
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/phrolova.JPEG")));
        // Character, weapon, levels, and dupes
        assertEquals(Character.getCharacterByID(1608), build.character);
        assertSame(Level.f80, build.characterLevel);
        assertEquals(0, build.chainLength);
        assertEquals(Weapon.getWeaponByID(21050066), build.weapon);
        assertEquals(Level.f80, build.weaponLevel);
        assertEquals(1, build.weaponRank);
        // Skills
        assertEquals(3, build.skillLevels[0]);
        assertEquals(5, build.skillLevels[1]);
        assertEquals(3, build.skillLevels[2]);
        assertEquals(8, build.skillLevels[3]);
        assertEquals(3, build.skillLevels[4]);
        // Echo 1
        assertEquals(6000115, build.echoes[0].echoID);
        assertEquals(19, build.echoes[0].sonataID);
        assertEquals(Stat.flatATK, build.echoes[0].mainStat);
        assertEquals(150, build.echoes[0].mainStatMagnitude);
        assertEquals(Stat.critDMG, build.echoes[0].secondStat);
        assertEquals(44, build.echoes[0].secondStatMagnitude);
        assertTrue(build.echoes[0].subStats.containsKey(Stat.skillBonus));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.critRate));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.energyRegen));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.flatATK));
        assertTrue(build.echoes[0].subStats.containsKey(Stat.percentHP));
        assertEquals(9.4f, build.echoes[0].subStats.get(Stat.skillBonus));
        assertEquals(9.3f, build.echoes[0].subStats.get(Stat.critRate));
        assertEquals(9.2f, build.echoes[0].subStats.get(Stat.energyRegen));
        assertEquals(40, build.echoes[0].subStats.get(Stat.flatATK));
        assertEquals(8.6f, build.echoes[0].subStats.get(Stat.percentHP));
        // Echo 2
        assertEquals(6000073, build.echoes[1].echoID);
        assertEquals(12, build.echoes[1].sonataID);
        assertEquals(Stat.flatATK, build.echoes[1].mainStat);
        assertEquals(100, build.echoes[1].mainStatMagnitude);
        assertEquals(Stat.havocBonus, build.echoes[1].secondStat);
        assertEquals(30, build.echoes[1].secondStatMagnitude);
        assertTrue(build.echoes[1].subStats.containsKey(Stat.critRate));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.ultBonus));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.percentATK));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.skillBonus));
        assertTrue(build.echoes[1].subStats.containsKey(Stat.percentDEF));
        assertEquals(7.5f, build.echoes[1].subStats.get(Stat.critRate));
        assertEquals(7.9f, build.echoes[1].subStats.get(Stat.ultBonus));
        assertEquals(8.6f, build.echoes[1].subStats.get(Stat.percentATK));
        assertEquals(7.9f, build.echoes[1].subStats.get(Stat.skillBonus));
        assertEquals(10.9f, build.echoes[1].subStats.get(Stat.percentDEF));
        // Echo 3
        assertEquals(6000081, build.echoes[2].echoID);
        assertEquals(12, build.echoes[2].sonataID);
        assertEquals(Stat.flatATK, build.echoes[2].mainStat);
        assertEquals(100, build.echoes[2].mainStatMagnitude);
        assertEquals(Stat.havocBonus, build.echoes[2].secondStat);
        assertEquals(30, build.echoes[2].secondStatMagnitude);
        assertTrue(build.echoes[2].subStats.containsKey(Stat.critRate));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.skillBonus));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.percentATK));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[2].subStats.containsKey(Stat.flatHP));
        assertEquals(6.3f, build.echoes[2].subStats.get(Stat.critRate));
        assertEquals(8.6f, build.echoes[2].subStats.get(Stat.skillBonus));
        assertEquals(11.6f, build.echoes[2].subStats.get(Stat.percentATK));
        assertEquals(470, build.echoes[2].subStats.get(Stat.flatHP));
        assertEquals(13.8f, build.echoes[2].subStats.get(Stat.critDMG));
        // Echo 4
        assertEquals(6000118, build.echoes[3].echoID);
        assertEquals(19, build.echoes[3].sonataID);
        assertEquals(Stat.flatHP, build.echoes[3].mainStat);
        assertEquals(2280, build.echoes[3].mainStatMagnitude);
        assertEquals(Stat.percentATK, build.echoes[3].secondStat);
        assertEquals(18, build.echoes[3].secondStatMagnitude);
        assertTrue(build.echoes[3].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.critRate));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.skillBonus));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.flatATK));
        assertTrue(build.echoes[3].subStats.containsKey(Stat.percentHP));
        assertEquals(12.6f, build.echoes[3].subStats.get(Stat.critDMG));
        assertEquals(7.5f, build.echoes[3].subStats.get(Stat.critRate));
        assertEquals(10.1f, build.echoes[3].subStats.get(Stat.skillBonus));
        assertEquals(30, build.echoes[3].subStats.get(Stat.flatATK));
        assertEquals(8.6f, build.echoes[3].subStats.get(Stat.percentHP));
        // Echo 5
        assertEquals(6000117, build.echoes[4].echoID);
        assertEquals(19, build.echoes[4].sonataID);
        assertEquals(Stat.flatHP, build.echoes[4].mainStat);
        assertEquals(2280, build.echoes[4].mainStatMagnitude);
        assertEquals(Stat.percentATK, build.echoes[4].secondStat);
        assertEquals(18, build.echoes[4].secondStatMagnitude);
        assertTrue(build.echoes[4].subStats.containsKey(Stat.critDMG));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.critRate));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.skillBonus));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.percentATK));
        assertTrue(build.echoes[4].subStats.containsKey(Stat.percentDEF));
        assertEquals(21, build.echoes[4].subStats.get(Stat.critDMG));
        assertEquals(6.9f, build.echoes[4].subStats.get(Stat.critRate));
        assertEquals(8.6f, build.echoes[4].subStats.get(Stat.skillBonus));
        assertEquals(8.6f, build.echoes[4].subStats.get(Stat.percentATK));
        assertEquals(8.1f, build.echoes[4].subStats.get(Stat.percentDEF));
    }
    
    @Test
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
}
