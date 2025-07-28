package ca.litten.discordbot.wuwabuilder;

import ca.litten.discordbot.wuwabuilder.parser.BuildParser;
import ca.litten.discordbot.wuwabuilder.wuwa.Build;
import ca.litten.discordbot.wuwabuilder.wuwa.ExtraData;
import ca.litten.discordbot.wuwabuilder.wuwa.StatPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(3)
public class CardBuilderTest {
    private static CardBuilder lightModeCardBuilder = new CardBuilder(true);
    
    @BeforeAll
    public static void Startup() {
        HakushinInterface.init(); // We need the images
    }
    
    @Test
    public void PhrolovaTest() throws IOException {
        // Compensate for my shit build
        Build phrolova = BuildParser.parseBuild(ImageIO.read(new File("testData/phrolova.JPEG")));
        phrolova.minorSkills[1] = false;
        phrolova.minorSkills[3] = false;
        phrolova.minorSkills[5] = false;
        phrolova.minorSkills[7] = false;
        BufferedImage card = lightModeCardBuilder.createCard(phrolova);
        ImageIO.write(card, "png", new File("testOut/phrolova.png"));
    }
    
    @Test
    public void ZaniTest() throws IOException {
        // Compensate for my shit build
        Build phrolova = BuildParser.parseBuild(ImageIO.read(new File("testData/zani.JPEG")));
        phrolova.minorSkills[1] = false;
        phrolova.minorSkills[3] = false;
        phrolova.minorSkills[5] = false;
        phrolova.minorSkills[7] = false;
        BufferedImage card = lightModeCardBuilder.createCard(phrolova);
        ImageIO.write(card, "png", new File("testOut/zani.png"));
    }
}
