import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class ARGBExtractor {
    private int initialWidth; // Tracks the width of the inital image, used to navigate the data buffer
    public ARGBExtractor(int iWidth) {
        initialWidth = iWidth;
    }

    // Takes an image and reads its ARGB int values into an array
    // Returns a 1d array with ARGB values of the image on it
    public int[] extractRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] result = new int[width*height];
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)  {
                result[y*width + x] = pixels[y*initialWidth + x];
            }
        }
        return result;
    }
}
