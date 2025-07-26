package ca.litten.discordbot.wuwabuilder.parser;

import ca.litten.discordbot.wuwabuilder.HakushinInterface;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FindClosestImageTest {
    @BeforeClass
    public static void Startup() {
        HakushinInterface.init(); // We need the images
    }
    
    @Test
    public void FindClosestSonataTest() throws IOException {
        Assert.assertEquals(12, FindClosestImage.findClosestSonata(
                ImageIO.read(new File("midnightVeil.jpg"))));
    }
    
    @Test
    public void FindClosestEchoInSonataTest() throws IOException {
        Assert.assertEquals(6000115, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("nmHecate.png")), 19));
        Assert.assertEquals(6000073, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("qNight.jpg")), 12));
    }
    
    @Test
    public void FindClosestEchoGlobalTest() throws IOException {
        Assert.assertEquals(6000115, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("nmHecate.png"))));
        Assert.assertEquals(6000073, FindClosestImage.findClosestEcho(
                ImageIO.read(new File("qNight.jpg"))));
    }
}
