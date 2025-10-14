package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import ca.litten.discordbot.wuwabuilder.wuwa.Weapon;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Order(0)
public class HakushinInterfaceTest {
    @BeforeAll
    public static void Startup() {
        WuwaDatabaseLoader.initFromHakushin();
    }
    
    @Test
    public void VerifyByID() {
        assertEquals("Sanhua", Character.getCharacterByID(1102).getName());
        assertEquals("Emerald of Genesis", Weapon.getWeaponByID(21020015).getName());
    }
}
