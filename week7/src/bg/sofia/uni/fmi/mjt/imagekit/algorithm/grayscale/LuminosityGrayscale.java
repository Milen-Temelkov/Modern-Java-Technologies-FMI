package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm {

    private static final double RED_COEFFICIENT = 0.21d;
    private static final double GREEN_COEFFICIENT = 0.72d;
    private static final double BLUE_COEFFICIENT = 0.07d;
    
    public  LuminosityGrayscale() { }

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

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                int gray = (int) (RED_COEFFICIENT * red + GREEN_COEFFICIENT * green + BLUE_COEFFICIENT * blue);

                Color grayColor = new Color(gray, gray, gray);
                grayscaleImage.setRGB(x, y, grayColor.getRGB());
            }
        }

        return grayscaleImage;
    }
}