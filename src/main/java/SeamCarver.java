import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SeamCarver {

    // For testing
    public static void main(String[] args) throws IOException {
        SeamCarver carver = new SeamCarver();
        carver.createEnergyArray("src/main/resources/images/hackersmoh.PNG");
    }

    // Creates an energy map from a filepath to an image
    // TODO: IOException handling
    private int[][] createEnergyArray(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedImage image = ImageIO.read(file);
        convertToRGB(image);
        return null;
    }

    // Takes a buffered image and converts into a 2d array with Color object for each pixel
    private Color[][] convertToRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = new Color(image.getRGB(x, y));
                System.out.println("Pixel " + x +", " + y + ":" + result[x][y]);
            }
        }
        return result;
    }
}
