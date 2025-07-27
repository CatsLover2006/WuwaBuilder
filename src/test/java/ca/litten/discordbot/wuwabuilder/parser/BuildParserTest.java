package ca.litten.discordbot.wuwabuilder.parser;

import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import ca.litten.discordbot.wuwabuilder.wuwa.*;
import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(2)
public class BuildParserTest {
    @BeforeAll
    public static void Startup() {
        HakushinInterface.init(); // We need the images
    }
    
    @Test
    public void PhrolovaTest() throws IOException {
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/phrolova.JPEG")));
        assertAll("Build Parsed Correctly",
                () -> assertEquals(Character.getCharacterByID(1608), build.character),
                () -> assertTrue(build.characterLevel == Level.f80 || build.characterLevel == Level.g80),
                () -> assertEquals(0, build.chainLength),
                () -> assertEquals(Weapon.getWeaponByID(21050066), build.weapon),
                () -> assertEquals(Level.f80, build.weaponLevel),
                () -> assertEquals(1, build.weaponRank),
                () -> assertEquals(6000115, build.echoes[0].echoID),
                () -> assertEquals(19, build.echoes[0].sonataID),
                () -> assertEquals(Stat.flatATK, build.echoes[0].mainStat),
                () -> assertEquals(150, build.echoes[0].mainStatMagnitude),
                () -> assertEquals(Stat.critDMG, build.echoes[0].secondStat),
                () -> assertEquals(44, build.echoes[0].secondStatMagnitude),
                () -> assertTrue(build.echoes[0].subStats.containsKey(Stat.skillBonus)),
                () -> assertTrue(build.echoes[0].subStats.containsKey(Stat.critRate)),
                () -> assertTrue(build.echoes[0].subStats.containsKey(Stat.energyRegen)),
                () -> assertTrue(build.echoes[0].subStats.containsKey(Stat.flatATK)),
                () -> assertTrue(build.echoes[0].subStats.containsKey(Stat.percentHP)),
                () -> assertEquals(9.4f, build.echoes[0].subStats.get(Stat.skillBonus)),
                () -> assertEquals(9.3f, build.echoes[0].subStats.get(Stat.critRate)),
                () -> assertEquals(9.2f, build.echoes[0].subStats.get(Stat.energyRegen)),
                () -> assertEquals(40, build.echoes[0].subStats.get(Stat.flatATK)),
                () -> assertEquals(8.6f, build.echoes[0].subStats.get(Stat.percentHP)),
                () -> assertEquals(6000073, build.echoes[1].echoID),
                () -> assertEquals(12, build.echoes[1].sonataID),
                () -> assertEquals(Stat.flatATK, build.echoes[1].mainStat),
                () -> assertEquals(100, build.echoes[1].mainStatMagnitude),
                () -> assertEquals(Stat.havocBonus, build.echoes[1].secondStat),
                () -> assertEquals(30, build.echoes[1].secondStatMagnitude),
                () -> assertTrue(build.echoes[1].subStats.containsKey(Stat.critRate)),
                () -> assertTrue(build.echoes[1].subStats.containsKey(Stat.ultBonus)),
                () -> assertTrue(build.echoes[1].subStats.containsKey(Stat.percentATK)),
                () -> assertTrue(build.echoes[1].subStats.containsKey(Stat.skillBonus)),
                () -> assertTrue(build.echoes[1].subStats.containsKey(Stat.percentDEF)),
                () -> assertEquals(7.5f, build.echoes[1].subStats.get(Stat.critRate)),
                () -> assertEquals(7.9f, build.echoes[1].subStats.get(Stat.ultBonus)),
                () -> assertEquals(8.6f, build.echoes[1].subStats.get(Stat.percentATK)),
                () -> assertEquals(7.9f, build.echoes[1].subStats.get(Stat.skillBonus)),
                () -> assertEquals(10.9f, build.echoes[1].subStats.get(Stat.percentDEF))
        );
    }
}
