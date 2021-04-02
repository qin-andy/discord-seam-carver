import energy.BackwardsEnergy;
import energy.EnergyStrategy;
import energy.ForwardsEnergy;
import pathfinder.DefaultPathfinder;
import pathfinder.ForwardsPathfinder;
import pathfinder.PathfinderStrategy;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class ModularCarver {
    private ImageHandler handler;
    private ARGBExtractor extractor;
    private EnergyStrategy energyMapper;
    private PathfinderStrategy pathfinder;
    private PathRemover pathRemover;

    private BufferedImage image;
    private int width;
    private int height;

    public static void main(String[] args) {
        String filePath = "src/main/resources/images/lapp.png";
        ModularCarver carver = new ModularCarver(filePath, new BackwardsEnergy(), new DefaultPathfinder());
        carver.carve(200, 0);
    }

    public ModularCarver(String imagePath, EnergyStrategy e, PathfinderStrategy p) {
        // Think about: should there be one carver for each image? or one carver to handle all images?
        handler = new ImageHandler();
        try {
            image = handler.read(imagePath);
        } catch (IOException e1) {

        }
        width = image.getWidth();
        height = image.getHeight();

        extractor = new ARGBExtractor(width);
        energyMapper = e;
        pathfinder = p;
        pathRemover = new PathRemover(width);
    }

    public void carve(int xCut, int yCut) {
        // Step 1: Convert the BufferedImage to TYPE_INT_ARGB
        if (image.getType() != 2) { // TOOD: double check if 2 corresponds to TYPE_INT_ARGB
            image = handler.convertToIntARGB(image);
        }

        // Step 2: Scale imgae if it is too big
        int maxSize = 1000;
        if (height > maxSize || width > maxSize) {
            long start = System.currentTimeMillis();
            double scale = (double) maxSize / Math.max(height, width);
            image = handler.scale(image, scale);
            xCut *= scale; // TODO: check if this scales the cut size correctly
            yCut *= scale;

            width = image.getWidth();
            height = image.getHeight();

            extractor.setInitialWidth(width);
            pathRemover.setInitialWidth(width);
        }

        int iWidth = width;

        // Step 3: Enter carving loop for vertical cuts
        for (int cutCount = 0; cutCount < xCut; cutCount++) {
            // Update width and height
            width = image.getWidth();
            height = image.getHeight();

            // Step 3a: Extract RGB values from image
            int[] RGBVals = extractor.extractARGB(image);
            //int[] RGBVals = convertToRGB(image, iWidth);

            // Step 3b: Create energy map based on RGB values
            int[] energy = energyMapper.calculateEnergy(RGBVals, width, height);
            //int[] energy = calculateEnergy(RGBVals, width, height);

            // Step 3c: Identify shortest energy path
            int[] shortestPath = pathfinder.shortestPath(energy, width, height);

            // Step 3d: Remove shortest path from image
            image = pathRemover.removePath(image, shortestPath);
        }

        // Step 4: TODO: transpose image and repeat for horizontal seams then transpose back

        // Step 5: Write image back to disc
        try {
            handler.save(image, "carved");
        } catch (IOException e) {

        }
    }
}

