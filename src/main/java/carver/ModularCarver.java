package carver;

import energy.EnergyStrategy;
import energy.ForwardsEnergy;
import manipulation.ImageHandler;
import manipulation.PathRemover;
import manipulation.RGBExtractor;
import pathfinder.ForwardsPathfinder;
import pathfinder.PathfinderStrategy;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ModularCarver {

    private static final int MAX_SIZE = 1000; // The maximum x or y length before the bot compresses the image

    private ImageHandler handler;
    private RGBExtractor extractor;
    private EnergyStrategy energyMapper;
    private PathfinderStrategy pathfinder;
    private PathRemover pathRemover;

    private BufferedImage image;
    private int width;
    private int height;

    private int convertToRGBTime;
    private int energyMapTime;
    private int shortestPathTime;
    private int pathRemovalTime;

    // For directly testing the Seam Carving
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String filePath = "src/main/resources/images/lapp.png"; //change this path to the desired image
        ModularCarver carver = new ModularCarver(filePath, new ForwardsEnergy(), new ForwardsPathfinder());
        carver.carve(200, 0); // change these values to the desired values
        System.out.println("Construction and carving took " + (System.currentTimeMillis() - start) + "ms!");
    }

    // Constructs a new carver.ModularCarver for an image using an EnergyStrategy and associated PathfinderStrategy
    public ModularCarver(String imagePath, EnergyStrategy e, PathfinderStrategy p) {
        handler = new ImageHandler();
        try {
            image = handler.read(imagePath);
        } catch (IOException e1) {
            System.out.println("Error reading file in carver.ModularCarver!");
        }
        width = image.getWidth();
        height = image.getHeight();

        extractor = new RGBExtractor(width);
        energyMapper = e;
        pathfinder = p;
        pathRemover = new PathRemover(width);

        // Initialize timing
        convertToRGBTime = 0;
        energyMapTime = 0;
        shortestPathTime = 0;
        pathRemovalTime = 0;
    }

    // Handle ratio cuts as proportioned flat cuts
    public void carve(double xRatio, double yRatio) {
        carve((int) (xRatio * width), (int) (yRatio * height));
    }

    // Uses the given energy strategy and pathfinder strategy to identify low energy seams
    // and remove them from the carver.ModularCarver's main image.
    public void carve(int xCut, int yCut) {
        // Step 1: Convert the BufferedImage to TYPE_INT_ARGB
        if (image.getType() != 2) {
            image = handler.convertToIntARGB(image);
        }

        // Step 2: Scale image if it is too big
        if (height > MAX_SIZE || width > MAX_SIZE) {
            long start = System.currentTimeMillis();
            double scale = (double) MAX_SIZE / Math.max(height, width);
            image = handler.scale(image, scale);
            xCut *= scale;
            yCut *= scale;

            width = image.getWidth();
            height = image.getHeight();
        }

        // Step 3: Enter carving loop for vertical cuts
        image = cutVerticalSeams(xCut);

        // Step 4: Transpose for horizontal cuts
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

        // Step 5: Write image back to file
        try {
            handler.save(image, "carved");
        } catch (IOException e) { System.out.println("Error writing file in carver.ModularCarver!"); }
    }

    // The main loop for cutting vertical seams
    private BufferedImage cutVerticalSeams(int numCuts) {
        int width = image.getWidth();
        int height = image.getHeight();
        extractor.setInitialWidth(width);
        pathRemover.setInitialWidth(width);
        long iterationTime = System.currentTimeMillis();
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

            // Print iteration time to track longer jobs
            if (cutCount % 50 == 0) {
                System.out.println("Iteration " + cutCount + " took " +
                        (System.currentTimeMillis() - iterationTime) + "ms!");
                iterationTime = System.currentTimeMillis();
            }
        }
        return image;
    }
}