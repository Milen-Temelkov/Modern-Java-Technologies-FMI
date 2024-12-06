package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LuminocityGrayscaleTest {

    private ImageAlgorithm grayscaleAlgorithm;

    @BeforeEach
    void setUp() {
        grayscaleAlgorithm = new LuminosityGrayscale();
    }

    @Test
    void testProcessWithNullImageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> grayscaleAlgorithm.process(null),
            "Expected process() to throw IllegalArgumentException when the input image is null");
    }

    @Test
    void testProcessWithSinglePixelImage() {
        BufferedImage inputImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Color inputColor = new Color(100, 150, 200);
        inputImage.setRGB(0, 0, inputColor.getRGB());

        BufferedImage result = grayscaleAlgorithm.process(inputImage);

        assertEquals(1, result.getWidth(), "Width of the result image should match input");
        assertEquals(1, result.getHeight(), "Height of the result image should match input");

        int grayValue = (int) (0.21 * 100 + 0.72 * 150 + 0.07 * 200);
        Color expectedGray = new Color(grayValue, grayValue, grayValue);

        assertEquals(expectedGray.getRGB(), result.getRGB(0, 0),
            "The grayscale pixel should match the expected luminosity value");
    }

    @Test
    void testProcessWithUniformColorImage() {
        BufferedImage inputImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        Color inputColor = new Color(50, 100, 150);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                inputImage.setRGB(x, y, inputColor.getRGB());
            }
        }

        BufferedImage result = grayscaleAlgorithm.process(inputImage);

        int grayValue = (int) (0.21 * 50 + 0.72 * 100 + 0.07 * 150);
        Color expectedGray = new Color(grayValue, grayValue, grayValue);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                assertEquals(expectedGray.getRGB(), result.getRGB(x, y),
                    "Each pixel should match the expected grayscale value");
            }
        }
    }

    @Test
    void testProcessWithGradientImage() {
        BufferedImage inputImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        inputImage.setRGB(0, 0, new Color(0, 0, 0).getRGB());
        inputImage.setRGB(1, 0, new Color(255, 0, 0).getRGB());
        inputImage.setRGB(0, 1, new Color(0, 255, 0).getRGB());
        inputImage.setRGB(1, 1, new Color(0, 0, 255).getRGB());

        BufferedImage result = grayscaleAlgorithm.process(inputImage);

        int blackGray = 0;
        int redGray = (int) (0.21 * 255);
        int greenGray = (int) (0.72 * 255);
        int blueGray = (int) (0.07 * 255);

        assertEquals(new Color(blackGray, blackGray, blackGray).getRGB(), result.getRGB(0, 0),
            "Pixel (0,0) should match the grayscale value for black");
        assertEquals(new Color(redGray, redGray, redGray).getRGB(), result.getRGB(1, 0),
            "Pixel (1,0) should match the grayscale value for red");
        assertEquals(new Color(greenGray, greenGray, greenGray).getRGB(), result.getRGB(0, 1),
            "Pixel (0,1) should match the grayscale value for green");
        assertEquals(new Color(blueGray, blueGray, blueGray).getRGB(), result.getRGB(1, 1),
            "Pixel (1,1) should match the grayscale value for blue");
    }

}
