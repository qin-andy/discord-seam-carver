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
    private RGBExtractor extractor;
    private EnergyStrategy energyMapper;
    private PathfinderStrategy pathfinder;
    private PathRemover pathRemover;

    private BufferedImage image;
    private int width;
    private int height;

    int convertToRGBTime = 0;
    int energyMapTime = 0;
    int shortestPathTime = 0;
    int pathRemovalTime = 0;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String filePath = "src/main/resources/images/lapp.png";
        ModularCarver carver = new ModularCarver(filePath, new BackwardsEnergy(), new DefaultPathfinder());
        carver.carve(300, 300);
        System.out.println("Construction and carving took " + (System.currentTimeMillis() - start) + "ms!");
    }

    public ModularCarver(String imagePath, EnergyStrategy e, PathfinderStrategy p) {
        // Think about: should there be one carver for each image? or one carver to handle all images?
        handler = new ImageHandler();
        try {
            image = handler.read(imagePath);
        } catch (IOException e1) {
            System.out.println("Error reading file!");
        }
        width = image.getWidth();
        height = image.getHeight();

        extractor = new RGBExtractor(width);
        energyMapper = e;
        pathfinder = p;
        pathRemover = new PathRemover(width);
    }

    public void carve(double xRatio, double yRatio) {
        carve((int) (xRatio * width), (int) (yRatio * height));
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
        }

        // Step 3: Enter carving loop for vertical cuts
        image = cutVerticalSeams(xCut);

        // Step 4: Tranpose for horizontal cuts
        if (yCut > 0) {
            image = handler.transpose(image);
            image = cutVerticalSeams(yCut);
            image = handler.transpose(image);
        }

        // Diagnostic Timing
        System.out.println("For a new image:");
        System.out.println("Convert to RGB Time: " + convertToRGBTime + " ms");
        System.out.println("Energy mapping Time: " + energyMapTime + " ms");
        System.out.println("Path identification Time: " + shortestPathTime + " ms");
        System.out.println("Path removal Time: " + pathRemovalTime + " ms");
        int totalTime = convertToRGBTime + energyMapTime + shortestPathTime + pathRemovalTime;
        System.out.println("TOTAL TIME:" + totalTime + " ms");

        // Step 5: Write image back to disc
        try {
            handler.save(image, "carved");
        } catch (IOException e) { }
    }

    private BufferedImage cutVerticalSeams(int numCuts) {
        int width = image.getWidth();
        int height = image.getHeight();
        extractor.setInitialWidth(width);
        pathRemover.setInitialWidth(width);
        for (int cutCount = 0; cutCount < numCuts; cutCount++) {
            // Step 3a: Extract RGB values from image
            long startTime = System.currentTimeMillis();
            int[] RGBVals = extractor.extractARGB(image);
            long endTime = System.currentTimeMillis();
            convertToRGBTime += (endTime - startTime);

            // Step 3b: Create energy map based on RGB values
            startTime = System.currentTimeMillis();
            int[] energy = energyMapper.calculateEnergy(RGBVals, width, height);
            endTime = System.currentTimeMillis();
            energyMapTime += (endTime - startTime);

            // Step 3c: Identify shortest energy path
            startTime = System.currentTimeMillis();
            int[] shortestPath = pathfinder.shortestPath(energy, width, height);
            endTime = System.currentTimeMillis();
            shortestPathTime+= (endTime - startTime);

            // Step 3d: Remove shortest path from image
            startTime = System.currentTimeMillis();
            image = pathRemover.removePath(image, shortestPath);
            endTime = System.currentTimeMillis();
            pathRemovalTime += (endTime - startTime);

            // Update width and height
            width = image.getWidth();
            height = image.getHeight();
        }
        return image;
    }
}