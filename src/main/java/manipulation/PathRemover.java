package manipulation;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class PathRemover {
    private int initialWidth;

    public PathRemover(int iWidth) {
        initialWidth = iWidth;
    }

    // Takes a BufferedImage and a vertical seam defined in a 1d array of X values
    // Returns a new BufferedImage with the vertical seam removed
    public BufferedImage removePath(BufferedImage image, int[] path) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = path[y]; x < image.getWidth() - 1; x++) {
                int position = y*initialWidth + x;
                pixels[position] = pixels[position+1];
            }
        }
        return image.getSubimage(0, 0, image.getWidth() - 1, image.getHeight());
    }

    // Reset the intial width value
    public void setInitialWidth(int newWidth) {
        initialWidth = newWidth;
    }
}
