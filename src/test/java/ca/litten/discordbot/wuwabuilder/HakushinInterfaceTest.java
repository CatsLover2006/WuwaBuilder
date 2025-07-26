package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.wuwa.Character;
import ca.litten.discordbot.wuwabuilder.wuwa.Weapon;
import org.junit.*;

public class HakushinInterfaceTest {
    @BeforeClass
    public static void Startup() {
        HakushinInterface.init();
    }
    
    @Test
    public void VerifyByID() {
        Assert.assertEquals("Sanhua", Character.getCharacterByID(1102).getName());
        Assert.assertEquals("Emerald of Genesis", Weapon.getWeaponByID(21020015).getName());
    }
}
