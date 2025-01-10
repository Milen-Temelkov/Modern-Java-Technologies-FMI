package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private final ImageAlgorithm grayscaleAlgorithm;

    private static final int COLOR_MAX_VALUE = 255;

    private static final int[][] HORIZONTAL_SOBEL_KERNEL = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    private static final int[][] VERTICAL_SOBEL_KERNEL = {
        {-1, -2, -1},
        {0,  0,  0},
        {1,  2,  1}
    };
    
    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    /**
     * Applies the image processing algorithm to the given image.
     *
     * @param image the image to be processed
     * @return BufferedImage the processed image of type (TYPE_INT_RGB)
     * @throws IllegalArgumentException if the image is null
     */
    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);

        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();

        BufferedImage edgeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                Color edgeColor = calculateEdgeColor(grayscaleImage, x, y);
                edgeImage.setRGB(x, y, edgeColor.getRGB());
            }
        }
        return edgeImage;
    }

    private Color calculateEdgeColor(BufferedImage grayscaleImage, int x, int y) {
        int dx = 0;
        int dy = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int pixelIntensity = new Color(grayscaleImage.getRGB(x + j, y + i)).getRed();
                dx += pixelIntensity * HORIZONTAL_SOBEL_KERNEL[i + 1][j + 1];
                dy += pixelIntensity * VERTICAL_SOBEL_KERNEL[i + 1][j + 1];
            }
        }

        int magnitude = (int) Math.sqrt(dx * dx + dy * dy);
        magnitude = Math.min(COLOR_MAX_VALUE, magnitude);

        return new Color(magnitude, magnitude, magnitude);
    }
}
