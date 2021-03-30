import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Set;

public class Carver {

    //TODO: remove testing main statement
    public static void main(String[] args) throws IOException { // For testing
        Carver carver = new Carver();
        carver.carve("src/main/resources/images/lapp.png", 200, 0);
    }


    // Creates an energy map from a filepath to an image
    public int carve(String filePath, int cutSize) throws IOException {

        // Reading file using ImageIO read
        File file = new File(filePath);
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            return -2;
        }

        int height = image.getHeight();
        int width = image.getWidth();
        System.out.println("Image size is " + width + " by " + height);

        // compression
        int maxSize = 1500;
        if (height > maxSize || width > maxSize) {
            long start = System.currentTimeMillis();
            double scale = (double) maxSize / Math.max(height, width);

            int newW = (int) (width*scale);
            int newH = (int) (height*scale);
            Image scaled = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            scaledImage.createGraphics().drawImage(scaled, 0, 0, null);
            scaledImage.createGraphics().dispose(); //TODO: CHECK IF THIS IS ACTUALLY DISPOSED!
            image = scaledImage;
            System.out.println("Image too large! Scaling down to " + newW + " by " + newH);

            double cutFactor = (double) cutSize / width;
            System.out.println("Adjusted cut size from " + cutSize + " to " + (int) (cutFactor * newW));
            cutSize = (int) (cutFactor * newW);

            long end = System.currentTimeMillis();
            System.out.println("Compression took " + (end - start) + "ms!");

        }

        // Conversion to ARGB
        BufferedImage imageARGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        imageARGB.createGraphics().drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);


        imageARGB.createGraphics().dispose();
        image = imageARGB;


        // Determining cut is possible (size)
        if (cutSize > image.getWidth()) {
            System.out.println("Cut size too big!");
            return -1;
        }

        // Timing
        int convertToRGBTime = 0;
        int energyMapTime = 0;
        int shortestSeamTime = 0;
        int pathRemovalTime = 0;
        long iterationStartTime = System.currentTimeMillis();


        int iWidth = image.getWidth();

        for (int i = 0; i < cutSize; i++) {
            long startTime = System.currentTimeMillis();
            //int[][] imageRGB = convertToRGB3(image);
            int[][] imageRGB = convertToRGB(image, iWidth);
            long endTime = System.currentTimeMillis();
            convertToRGBTime += (endTime - startTime);

            startTime = System.currentTimeMillis();
            int[][] energyMap = createEnergyMap(imageRGB);
            endTime = System.currentTimeMillis();
            energyMapTime += (endTime - startTime);

            startTime = System.currentTimeMillis();
            int[] path = shortestPath(energyMap);
            endTime = System.currentTimeMillis();
            shortestSeamTime += (endTime - startTime);

            startTime = System.currentTimeMillis();
            image = removePath(path, image, iWidth);
            endTime = System.currentTimeMillis();
            pathRemovalTime += (endTime - startTime);

            //Timing for iterations
            if (i%25 == 0) {
                long iterationEndTime = System.currentTimeMillis();
                System.out.println("Iteration " + i + " took " + (iterationEndTime - iterationStartTime) + " milliseconds");
                iterationStartTime = System.currentTimeMillis();
            }
        }

        System.out.println("For a new image:");
        System.out.println("Convert to RGB Time: " + convertToRGBTime + " ms");
        System.out.println("Energy mapping Time: " + energyMapTime + " ms");
        System.out.println("Path identification Time: " + shortestSeamTime + " ms");
        System.out.println("Path removal Time: " + pathRemovalTime + " ms");
        int totalTime = convertToRGBTime + energyMapTime + shortestSeamTime + pathRemovalTime;
        System.out.println("TOTAL TIME:" + totalTime + " ms");


        File outputFile = new File("src/main/resources/images/carved.PNG");
        ImageIO.write(image, "PNG", outputFile);
        return totalTime;
    }

    public int carve(String filePath, int cutSize, int cutSizeY) throws IOException {

        // Reading file using ImageIO read
        File file = new File(filePath);
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            return -2;
        }

        int height = image.getHeight();
        int width = image.getWidth();
        System.out.println("Image size is " + width + " by " + height);
        System.out.println("Requested cut is " + cutSize + " and " + cutSizeY);

        if (cutSize >= image.getWidth() || cutSizeY >= image.getHeight()) {
            System.out.println("Cut size too big!");
            return -1;
        }

        // compression
        int maxSize = 1000;
        if (height > maxSize || width > maxSize) {
            long start = System.currentTimeMillis();
            double scale = (double) maxSize / Math.max(height, width);

            int newW = (int) (width*scale);
            int newH = (int) (height*scale);
            Image scaled = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            scaledImage.createGraphics().drawImage(scaled, 0, 0, null);
            scaledImage.createGraphics().dispose(); //TODO: CHECK IF THIS IS ACTUALLY DISPOSED!
            image = scaledImage;
            System.out.println("Image too large! Scaling down to " + newW + " by " + newH);

            double cutFactorX = (double) cutSize / width;
            double cutFactorY = (double) cutSizeY / height;

            cutSize = (int) (cutFactorX * newW);
            cutSizeY = (int) (cutFactorY * newH);
            System.out.println("Adjusted cut size to " + cutSize + " and " + cutSizeY);
            height = newH;
            width = newW;

            long end = System.currentTimeMillis();
            System.out.println("Compression took " + (end - start) + "ms!");

        }

        // Conversion to ARGB
        BufferedImage imageARGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        imageARGB.createGraphics().drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);


        imageARGB.createGraphics().dispose();
        image = imageARGB;


        // Determining cut is possible (size)
        if (cutSize > image.getWidth()) {
            System.out.println("Cut size too big!");
            return -1;
        }

        // Timing
        int convertToRGBTime = 0;
        int energyMapTime = 0;
        int shortestSeamTime = 0;
        int pathRemovalTime = 0;
        long iterationStartTime = System.currentTimeMillis();


        int iWidth = image.getWidth();

        for (int i = 0; i < cutSize; i++) {
            long startTime = System.currentTimeMillis();
            //int[][] imageRGB = convertToRGB3(image);
            int[][] imageRGB = convertToRGB(image, iWidth);
            long endTime = System.currentTimeMillis();
            convertToRGBTime += (endTime - startTime);

            startTime = System.currentTimeMillis();
            int[][] energyMap = createEnergyMap(imageRGB);
            endTime = System.currentTimeMillis();
            energyMapTime += (endTime - startTime);

            startTime = System.currentTimeMillis();
            int[] path = shortestPath(energyMap);
            endTime = System.currentTimeMillis();
            shortestSeamTime += (endTime - startTime);

            startTime = System.currentTimeMillis();
            image = removePath(path, image, iWidth);
            endTime = System.currentTimeMillis();
            pathRemovalTime += (endTime - startTime);

            //Timing for iterations
            if (i%50 == 0) {
                long iterationEndTime = System.currentTimeMillis();
                System.out.println("Iteration " + i + " took " + (iterationEndTime - iterationStartTime) + " milliseconds");
                iterationStartTime = System.currentTimeMillis();
            }
        }


        if (cutSizeY > 0) {
            long start = System.currentTimeMillis();

            BufferedImage transpose = new BufferedImage(image.getHeight(), image.getWidth(),
                    BufferedImage.TYPE_INT_ARGB);

            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    transpose.setRGB(y, x, image.getRGB(x, y));
                }
            }
            image = transpose;

            long end = System.currentTimeMillis();
            System.out.println("Tranposing took  " + (end - start) + "ms!");
            iWidth = image.getWidth();
            for (int i = 0; i < cutSizeY; i++) {
                long startTime = System.currentTimeMillis();
                //int[][] imageRGB = convertToRGB3(image);
                int[][] imageRGB = convertToRGB(image, iWidth);
                long endTime = System.currentTimeMillis();
                convertToRGBTime += (endTime - startTime);

                startTime = System.currentTimeMillis();
                int[][] energyMap = createEnergyMap(imageRGB);
                endTime = System.currentTimeMillis();
                energyMapTime += (endTime - startTime);

                startTime = System.currentTimeMillis();
                int[] path = shortestPath(energyMap);
                endTime = System.currentTimeMillis();
                shortestSeamTime += (endTime - startTime);

                startTime = System.currentTimeMillis();
                image = removePath(path, image, iWidth);
                endTime = System.currentTimeMillis();
                pathRemovalTime += (endTime - startTime);

                //Timing for iterations
                if (i%50 == 0) {
                    long iterationEndTime = System.currentTimeMillis();
                    System.out.println("Iteration " + i + " took " + (iterationEndTime - iterationStartTime) + " milliseconds");
                    iterationStartTime = System.currentTimeMillis();
                }
            }

            transpose = new BufferedImage(image.getHeight(), image.getWidth(),
                    BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    transpose.setRGB(y, x, image.getRGB(x, y));
                }
            }
            image = transpose;
        }


        System.out.println("For a new image:");
        System.out.println("Convert to RGB Time: " + convertToRGBTime + " ms");
        System.out.println("Energy mapping Time: " + energyMapTime + " ms");
        System.out.println("Path identification Time: " + shortestSeamTime + " ms");
        System.out.println("Path removal Time: " + pathRemovalTime + " ms");
        int totalTime = convertToRGBTime + energyMapTime + shortestSeamTime + pathRemovalTime;
        System.out.println("TOTAL TIME:" + totalTime + " ms");


        File outputFile = new File("src/main/resources/images/carved.PNG");
        ImageIO.write(image, "PNG", outputFile);
        return totalTime;
    }

    // I guess when subimages are taken in remove path, the underlying databuffer dimensions aren't changed.
    // we account for that by storing the intiial width as iWidth and using it to map 1d array to 2d
    private int[][] convertToRGB(BufferedImage image, int iWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[width][height];
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)  {
                result[x][y] = pixels[y*iWidth + x];
            }
        }
        return result;
    }

    private int[][] createEnergyMap(int[][] colorArray) {
        int width = colorArray.length;
        int height = colorArray[0].length;
        int[][] energyArray = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)  {
                // Typed out for readability
                // Energy mapping algorithm is Δx^2(x, y) + Δy^2(x, y)
                int prev;
                int next;

                // Edges are trated as adjacent to the opposite side
                if (x == 0) {
                    prev = colorArray[width-1][y];
                    next = colorArray[x+1][y];
                } else if (x == width - 1) {
                    prev = colorArray[x-1][y];
                    next = colorArray[0][y];
                } else {
                    prev = colorArray[x-1][y];
                    next = colorArray[x+1][y];
                }


                int pB = prev & 0xFF;
                int pG = (prev & 0xFF) >> 8;
                int pR = (prev & 0xFF) >> 16;

                int nB = next & 0xFF;
                int nG = (next & 0xFF) >> 8;
                int nR = (next & 0xFF) >> 16;

                int deltaR = (int) Math.pow(pR - nR, 2);
                int deltaG = (int) Math.pow(pG - nG, 2);
                int deltaB = (int) Math.pow(pB - nB, 2);

                int xDeltaSquare = deltaR + deltaG + deltaB;

                if (y == 0) {
                    prev = colorArray[x][height-1];
                    next = colorArray[x][y+1];
                } else if (y == height - 1) {
                    prev = colorArray[x][y-1];
                    next = colorArray[x][0];
                } else {
                    prev = colorArray[x][y];
                    next = colorArray[x][y];
                }

                pB = prev & 0xFF;
                pG = (prev & 0xff) >> 8;
                pR = (prev & 0xff) >> 16;

                nB = next & 0xFF;
                nG = (next & 0xFF) >> 8;
                nR = (next & 0xFF) >> 16;

                deltaR = (int) Math.pow(pR - nR, 2);
                deltaG = (int) Math.pow(pG - nG, 2);
                deltaB = (int) Math.pow(pB - nB, 2);

                int yDeltaSquare = deltaR + deltaG + deltaB;
                energyArray[x][y] = xDeltaSquare + yDeltaSquare;
            }
        }

        return energyArray;
    }

    private int[] shortestPath(int[][] energyArray) {
        int width = energyArray.length;
        int height = energyArray[0].length;

        // Each [x][y] pair is modelled as a node
        int[][] parent = new int[width][height]; // The x value of the node's parent; the y is the child y - 1
        int[][] distTo = new int[width][height]; // The shortest distance to the node
        for (int i = 0; i < width; i++) {
            Arrays.fill(distTo[i], Integer.MAX_VALUE);
            distTo[i][0] = energyArray[i][0];
        }

        int newDist = 0;
        // Traverse through every node in order for a weighted path tree
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                int lower = -1;
                int upper = 1;
                if (x == 0) {
                    lower = 0;
                } else if (x == width - 1) {
                    upper = 0;
                }

                // For each of the current node's children, check if the path through the current node is shorter
                for (int i = lower; i < upper + 1; i++) {
                    newDist = distTo[x][y] + energyArray[x+i][y+1];
                    if (newDist < distTo[x+i][y+1]) {
                        distTo[x+i][y+1] = newDist;
                        parent[x+i][y+1] = x;
                    }
                }
            }
        }

        // Backtracking from the minimum of the last row:
        // Finding minimum of the last row:
        int minEnergy = distTo[0][height-1];
        int minX = 0;

        for (int x = 1; x < width; x++) {
            if (distTo[x][height-1] < minEnergy) {
                minX = x;
                minEnergy = distTo[x][height-1];
            }
        }

        // Start the minPath array to store the x values of the minimum path, with the indicies indicating the y coord
        int[] minPath = new int[height];
        minPath[height-1] = minX;

        int childX = minX;

        int parentX;
        for (int y = height - 1; y > 0; y--) {
            parentX = parent[childX][y];
            minPath[y-1] = parentX;
            childX = parentX;
        }
        return minPath;
    }

    private BufferedImage removePath(int[] path, BufferedImage image, int iWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = path[y]; x < image.getWidth() - 1; x++) {
                int position = y*iWidth + x;
                pixels[position] = pixels[position+1];
            }
        }
        return image.getSubimage(0, 0, image.getWidth() - 1, image.getHeight());
    }
}

