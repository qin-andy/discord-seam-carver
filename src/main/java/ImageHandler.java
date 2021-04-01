import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageHandler {

    // Reads an image to a BufferedImage object
    public BufferedImage read(String path) throws IOException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {

        }
        return image;
    }

    // Scales a BufferedImage to the desired proportion of the original using Graphics2D
    public BufferedImage scale(BufferedImage image, double factor) {
        int height = image.getHeight();
        int width = image.getWidth();
        long start = System.currentTimeMillis();

        int newW = (int) (width*factor);
        int newH = (int) (height*factor);
        Image scaled = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage scaledImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose(); //TODO: CHECK IF THIS IS ACTUALLY DISPOSED!

        System.out.println("Image too large! Scaling down to " + newW + " by " + newH);
        long end = System.currentTimeMillis();
        System.out.println("Compression took " + (end - start) + "ms!");
        return scaledImage;
    }

    // Returns a copy of the input BufferedImage of type TYPE_INT_ARGB
    public BufferedImage convertToIntARGB(BufferedImage image) {
        BufferedImage imageARGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imageARGB.createGraphics();
        imageARGB.createGraphics().drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        return imageARGB;
    }

    // Returns a transposed version of the BufferedImage supplied
    public BufferedImage transpose (BufferedImage image) {
        long start = System.currentTimeMillis();
        BufferedImage transpose = new BufferedImage(image.getHeight(), image.getWidth(),
                BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                transpose.setRGB(y, x, image.getRGB(x, y));
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Tranposing took  " + (end - start) + "ms!");
        return transpose;
    }

    // Saves the given BufferedImage as a PNG in the images folder with the given name
    public void save(BufferedImage image, String name) {
        File outputFile = new File("src/main/resources/images/" + name + ".PNG");
        ImageIO.write(image, "PNG", outputFile);
    }
}
