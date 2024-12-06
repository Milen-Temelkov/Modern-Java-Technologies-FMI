package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SobelEdgeDetectionTest {

    private SobelEdgeDetection sobelEdgeDetection;
    private ImageAlgorithm mockGrayscaleAlgorithm;

    @BeforeEach
    void setUp() {
        mockGrayscaleAlgorithm = Mockito.mock(ImageAlgorithm.class);
        sobelEdgeDetection = new SobelEdgeDetection(mockGrayscaleAlgorithm);
    }

    @Test
    void testProcessWithNullImageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> sobelEdgeDetection.process(null),
            "Expected process() to throw IllegalArgumentException when the input image is null");
    }

    @Test
    void testProcessWithSinglePixelImage() {
        BufferedImage inputImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        inputImage.setRGB(0, 0, Color.BLACK.getRGB());

        when(mockGrayscaleAlgorithm.process(inputImage)).thenReturn(inputImage);

        BufferedImage result = sobelEdgeDetection.process(inputImage);

        assertEquals(1, result.getWidth(), "Width of the result image should match input");
        assertEquals(1, result.getHeight(), "Height of the result image should match input");
        assertEquals(Color.BLACK.getRGB(), result.getRGB(0, 0), "The pixel value should remain black");
    }

    @Test
    void testProcessWithEdgeImage() {
        BufferedImage inputImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

        inputImage.setRGB(0, 0, new Color(50, 50, 50).getRGB());
        inputImage.setRGB(0, 1, new Color(50, 50, 50).getRGB());
        inputImage.setRGB(0, 2, new Color(50, 50, 50).getRGB());

        inputImage.setRGB(1, 0, new Color(255, 255, 255).getRGB());
        inputImage.setRGB(1, 1, new Color(255, 255, 255).getRGB());
        inputImage.setRGB(1, 2, new Color(255, 255, 255).getRGB());

        inputImage.setRGB(2, 0, new Color(100, 100, 100).getRGB());
        inputImage.setRGB(2, 1, new Color(100, 100, 100).getRGB());
        inputImage.setRGB(2, 2, new Color(100, 100, 100).getRGB());

        when(mockGrayscaleAlgorithm.process(inputImage)).thenReturn(inputImage);

        BufferedImage result = sobelEdgeDetection.process(inputImage);

        assertNotNull(result, "Resulting image should not be null");
        assertEquals(3, result.getWidth(), "Width of the result image should match input");
        assertEquals(3, result.getHeight(), "Height of the result image should match input");

        Color edgeColor = new Color(result.getRGB(1, 1));
        assertTrue(edgeColor.getRed() > 0, "The detected edge should have a non-zero intensity");
    }

    @Test
    void testProcessHandlesEdgesCorrectly() {
        BufferedImage inputImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                int gray = x * 50;
                inputImage.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }

        when(mockGrayscaleAlgorithm.process(inputImage)).thenReturn(inputImage);

        BufferedImage result = sobelEdgeDetection.process(inputImage);

        assertNotNull(result, "Resulting image should not be null");
        for (int x = 1; x < 4; x++) {
            for (int y = 1; y < 4; y++) {
                Color edgeColor = new Color(result.getRGB(x, y));
                assertTrue(edgeColor.getRed() > 0, "Edge intensity should be detected");
            }
        }
    }
}
