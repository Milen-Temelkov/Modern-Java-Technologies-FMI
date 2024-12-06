package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalFileSystemImageManagerTest {
    private FileSystemImageManager imageManager;

    @BeforeAll
    static void setUpMock() {
        mockStatic(ImageIO.class);
    }

    @BeforeEach
    void setUp() {
        imageManager = new LocalFileSystemImageManager();
    }



    @Test
    void testLoadImageValid() throws IOException {
        File validFile = mock(File.class);
        BufferedImage validImage = mock(BufferedImage.class);

        when(validFile.exists()).thenReturn(true);
        when(validFile.isFile()).thenReturn(true);
        when(validFile.getPath()).thenReturn("validImage.jpg");

        when(ImageIO.read(validFile)).thenReturn(validImage);

        BufferedImage image = imageManager.loadImage(validFile);

        assertNotNull(image, "Loaded image should not be null");
    }

    @Test
    void testLoadImageInvalidFile() {
        File invalidFile = mock(File.class);
        when(invalidFile.exists()).thenReturn(false);
        when(invalidFile.isFile()).thenReturn(false);

        assertThrows(IOException.class, () -> imageManager.loadImage(invalidFile),
            "IOException expected when file is invalid");
    }

    @Test
    void testLoadImageNullFile() {
        assertThrows(IllegalArgumentException.class, () -> imageManager.loadImage(null),
            "IllegalArgumentException expected when file is null");
    }

    @Test
    void testLoadImageFileNotSupported() throws IOException {
        File invalidFile = mock(File.class);
        when(invalidFile.exists()).thenReturn(true);
        when(invalidFile.isFile()).thenReturn(true);
        when(invalidFile.getPath()).thenReturn("invalidImage.xyz");

        when(ImageIO.read(invalidFile)).thenReturn(null);

        assertThrows(IOException.class, () -> imageManager.loadImage(invalidFile),
            "IOException expected when the file format is not supported");
    }

    @Test
    void testLoadImagesFromDirectoryValid() throws IOException {
        File validDir = mock(File.class);
        File validImage1 = mock(File.class);
        File validImage2 = mock(File.class);

        BufferedImage image1 = mock(BufferedImage.class);
        BufferedImage image2 = mock(BufferedImage.class);

        when(validDir.exists()).thenReturn(true);
        when(validDir.isDirectory()).thenReturn(true);

        File[] files = { validImage1, validImage2 };
        when(validDir.listFiles()).thenReturn(files);

        when(validImage1.isFile()).thenReturn(true);
        when(validImage2.isFile()).thenReturn(true);
        when(validImage1.getPath()).thenReturn("image1.jpg");
        when(validImage2.getPath()).thenReturn("image2.jpg");
        when(validImage1.getName()).thenReturn("image1.jpg");
        when(validImage2.getName()).thenReturn("image2.jpg");
        when(validImage1.exists()).thenReturn(true);
        when(validImage2.exists()).thenReturn(true);

        when(ImageIO.read(validImage1)).thenReturn(image1);
        when(ImageIO.read(validImage2)).thenReturn(image2);

        List<BufferedImage> images = imageManager.loadImagesFromDirectory(validDir);

        assertEquals(2, images.size(), "Should load two images");
        assertTrue(images.contains(image1), "Should contain first image");
        assertTrue(images.contains(image2), "Should contain second image");
    }

    @Test
    void testLoadImagesInvalidDirectory() {
        File invalidDir = mock(File.class);

        when(invalidDir.exists()).thenReturn(false);
        when(invalidDir.isDirectory()).thenReturn(false);

        assertThrows(IOException.class, () -> imageManager.loadImagesFromDirectory(invalidDir),
            "IOException expected when directory is invalid");
    }

    @Test
    void testLoadImagesNullDirectory() {
        assertThrows(IllegalArgumentException.class, () -> imageManager.loadImagesFromDirectory(null),
            "IllegalArgumentException expected when directory is null");
    }

    @Test
    void testSaveImageNullFile() {
        BufferedImage image = mock(BufferedImage.class);

        assertThrows(IllegalArgumentException.class, () -> imageManager.saveImage(image, null),
            "IllegalArgumentException expected when file is null");
    }

    @Test
    void testSaveImageNullImage() {
        File existingFile = mock(File.class);

        assertThrows(IllegalArgumentException.class, () -> imageManager.saveImage(null, existingFile),
            "IllegalArgumentException expected when image is null");
    }

    @Test
    void testSaveImageFileAlreadyExists() {
        BufferedImage image = mock(BufferedImage.class);
        File existingFile = mock(File.class);

        when(existingFile.exists()).thenReturn(true);
        when(existingFile.getPath()).thenReturn("existingImage.jpg");

        assertThrows(IOException.class, () -> imageManager.saveImage(image, existingFile),
            "IOException expected when file already exists");
    }

    @Test
    void testSaveImageInvalidFormat() {
        BufferedImage image = mock(BufferedImage.class);
        File invalidFile = mock(File.class);

        when(invalidFile.exists()).thenReturn(false);
        when(invalidFile.getName()).thenReturn("invalidImage.xyz");

        assertThrows(IOException.class, () -> imageManager.saveImage(image, invalidFile),
            "IOException expected for invalid file format");
    }

}
