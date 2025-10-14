package ca.litten.discordbot.wuwabuilder.parser;

import ca.litten.discordbot.wuwabuilder.WuwaDatabase;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
public class FindClosestImageTest {
    @BeforeAll
    public static void Startup() {
        WuwaDatabase.initFromHakushin(); // We need the images
    }
    
    @Test
    @Order(1)
    public void FindClosestSonataTest() throws IOException {
        assertEquals(12, FindClosestImage.findClosestSonata(
                ImageIO.read(new File("testData/sonata/midnightVeil.jpg"))));
        assertEquals(19, FindClosestImage.findClosestSonata(
                ImageIO.read(new File("testData/sonata/dreamOfTheLost.png"))));
        assertEquals(11, FindClosestImage.findClosestSonata(
                ImageIO.read(new File("testData/sonata/eternalRadance.jpg"))));
    }
    
    @Test
    @Order(2)
    public void FindClosestEchoInSonataTest() throws IOException {
        assertEquals(6000115, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("testData/nmHecate.png")), 19));
        assertEquals(6000073, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("testData/qNight.jpg")), 12));
    }
    
    @Test
    @Order(3)
    public void FindClosestEchoGlobalTest() throws IOException {
        assertEquals(6000115, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("testData/nmHecate.png"))));
        assertEquals(6000073, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("testData/qNight.jpg"))));
    }
}
