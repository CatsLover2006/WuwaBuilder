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
    private static CardBuilder lightModeCardBuilder;
    
    @BeforeAll
    public static void Startup() {
        HakushinInterface.init(); // We need the images
        lightModeCardBuilder = new CardBuilder(true);
    }
    
    @Test
    public void PhrolovaTest() throws IOException {
        // Compensate for my shit build
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/phrolova.JPEG")));
        build.minorSkills[1] = false;
        build.minorSkills[3] = false;
        build.minorSkills[5] = false;
        build.minorSkills[7] = false;
        BufferedImage card = lightModeCardBuilder.createCard(build);
        ImageIO.write(card, "png", new File("testOut/phrolova.png"));
    }
    
    @Test
    public void ZaniTest() throws IOException {
        // Compensate for my shit build
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/zani.JPEG")));
        for (int i = 0; i < 8; i++) build.minorSkills[i] = false;
        build.minorSkills[2] = true;
        build.minorSkills[4] = true;
        BufferedImage card = lightModeCardBuilder.createCard(build);
        ImageIO.write(card, "png", new File("testOut/zani.png"));
    }
    
    @Test
    public void SanhuaTest() throws IOException {
        // Compensate for my shit build
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/sanhua.JPEG")));
        for (int i = 0; i < 8; i++) build.minorSkills[i] = false;
        BufferedImage card = lightModeCardBuilder.createCard(build);
        ImageIO.write(card, "png", new File("testOut/sanhua.png"));
    }
    
    @Test
    public void VerinaTest() throws IOException {
        // Compensate for my shit build
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/verina.JPEG")));
        for (int i = 0; i < 8; i++) build.minorSkills[i] = false;
        BufferedImage card = lightModeCardBuilder.createCard(build);
        ImageIO.write(card, "png", new File("testOut/verina.png"));
    }
    
    @Test
    public void CartethyiaTest() throws IOException {
        // Compensate for my shit build
        Build build = BuildParser.parseBuild(ImageIO.read(new File("testData/cartethyia.jpeg")));
        build.minorSkills[1] = false;
        build.minorSkills[7] = false;
        BufferedImage card = lightModeCardBuilder.createCard(build);
        ImageIO.write(card, "png", new File("testOut/cartethyia.png"));
    }
}
