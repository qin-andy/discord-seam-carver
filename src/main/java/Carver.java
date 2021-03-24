import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Carver {

    public static void main(String[] args) throws IOException { // For testing
        Carver carver = new Carver();
        carver.createEnergyArray("src/main/resources/images/cardboard_why.PNG");
    }

    // Creates an energy map from a filepath to an image
    private int[][] createEnergyArray(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedImage image = ImageIO.read(file);
        Color[][] imageRGB = convertToRGB(image);
        createEnergyMap(imageRGB);

        return null;
    }

    // Takes a buffered image and converts into a 2d array with Color object for each pixel
    // TODO: Handle alpha channel
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

    private int[][] createEnergyMap(Color[][] colorArray) {
        int width = colorArray.length;
        int height = colorArray[0].length;
        int[][] energyArray = new int[width][height];
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                // Typed out for readability
                // Energy mapping algorithm is Δx^2(x, y) + Δy^2(x, y)
                Color prev = colorArray[x-1][y];
                Color next = colorArray[x+1][y];
                int deltaR = (int) Math.pow(prev.getRed() - next.getRed(), 2);
                int deltaG = (int) Math.pow(prev.getGreen() - next.getGreen(), 2);
                int deltaB = (int) Math.pow(prev.getBlue() - next.getBlue(), 2);

                int xDeltaSquare = deltaR + deltaG + deltaB;

                prev = colorArray[x][y-1];
                next = colorArray[x][y+1];
                deltaR = (int) Math.pow(prev.getRed() - next.getRed(), 2);
                deltaG = (int) Math.pow(prev.getGreen() - next.getGreen(), 2);
                deltaB = (int) Math.pow(prev.getBlue() - next.getBlue(), 2);

                int yDeltaSquare = deltaR + deltaG + deltaB;
                energyArray[x][y] = xDeltaSquare + yDeltaSquare;

                // Debug to test
                System.out.print("[" + x +"," + y + ":" + energyArray[x][y] + "] ");
            }
            System.out.println();
        }
        return energyArray;
    }
}
