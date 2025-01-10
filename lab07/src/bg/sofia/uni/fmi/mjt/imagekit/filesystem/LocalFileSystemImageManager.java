package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalFileSystemImageManager implements FileSystemImageManager {

    public LocalFileSystemImageManager() { }

    /**
     * Loads a single image from the given file path.
     *
     * @param imageFile the file containing the image.
     * @return the loaded BufferedImage.
     * @throws IllegalArgumentException if the file is null
     * @throws IOException              if the file does not exist, is not a regular file,
     *                                  or is not in one of the supported formats.
     */
    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        validateFile(imageFile);

        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                throw new IOException("The file format is not supported or the file is corrupted: "
                    + imageFile.getPath());
            }
            return image;
        } catch (IOException e) {
            throw new IOException("Failed to load image from file: " + imageFile.getPath(), e);
        }
    }

    private void validateFile(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("The specified file does not exist or is not a regular file: " + imageFile.getPath());
        }
    }


    /**
     * Loads all images from the specified directory.
     *
     * @param imagesDirectory the directory containing the images.
     * @return A list of BufferedImages representing the loaded images.
     * @throws IllegalArgumentException if the directory is null.
     * @throws IOException              if the directory does not exist, is not a directory,
     *                                  or contains files that are not in one of the supported formats.
     */
    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        validateDirectory(imagesDirectory);

        List<BufferedImage> images = new ArrayList<>();
        File[] files = imagesDirectory.listFiles();

        if (files == null) {
            throw new IOException("Failed to retrieve files from directory: " + imagesDirectory.getPath());
        }

        for (File file : files) {
            if (file.isFile()) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    if (image != null) {
                        images.add(image);
                    } else {
                        throw new IOException("Unsupported or corrupted image file: " + file.getName());
                    }
                } catch (IOException e) {
                    throw new IOException("Error reading file: " + file.getName() + " (" + e.getMessage() + ")");
                }
            }
        }

        return images;
    }

    private void validateDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }

        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("The specified path is not a valid directory: " + imagesDirectory.getPath());
        }
    }

    /**
     * Saves the given image to the specified file path.
     *
     * @param image     the image to save.
     * @param imageFile the file to save the image to.
     * @throws IllegalArgumentException if the image or file is null.
     * @throws IOException              if the file already exists or the parent directory does not exist.
     */
    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (imageFile == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        String format = getFormat(imageFile);

        try {
            boolean success = ImageIO.write(image, format, imageFile);
            if (!success) {
                throw new IOException("Failed to save image. Unsupported format: " + format);
            }
        } catch (IOException e) {
            throw new IOException("Failed to save the image to file: " + imageFile.getPath(), e);
        }
    }

    private static String getFormat(File imageFile) throws IOException {
        File parentDir = imageFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            throw new IOException("The parent directory does not exist: " + parentDir.getPath());
        }

        if (imageFile.exists()) {
            throw new IOException("File already exists: " + imageFile.getPath());
        }

        String fileName = imageFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("File must have a valid extension (.png, .jpg, .bmp)");
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}
