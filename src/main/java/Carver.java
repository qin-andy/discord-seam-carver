import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Carver {

    //TODO: remove testing main statement
    public static void main(String[] args) throws IOException { // For testing
        Carver carver = new Carver();
        carver.carve("src/main/resources/images/hackersmoh.PNG", 200 );
    }

    // Creates an energy map from a filepath to an image
    public int carve(String filePath, int cutSize) throws IOException {

        File file = new File(filePath);
        BufferedImage image = ImageIO.read(file);
        if (cutSize > image.getWidth()) {
            System.out.println("Cut size too big!");
            return -1;
        }
        int convertToRGBTime = 0;
        int energyMapTime = 0;
        int shortestSeamTime = 0;
        int pathRemovalTime = 0;

        for (int i = 0; i < cutSize; i++) {
            System.out.print("Working on seam " + i + "...");
            long startTime = System.currentTimeMillis();
            Color[][] imageRGB = convertToRGB(image);
            //long endTime = System.currentTimeMillis();
            //System.out.println("RGB Conversion for " + i + " took " + (endTime - startTime) + " milliseconds");
            //convertToRGBTime += (endTime - startTime);
            //System.out.print("converted RGB...");

            //startTime = System.currentTimeMillis();
            int[][] energyMap = createEnergyMap(imageRGB);
            //System.out.println("Pathfinding for " + i + " took " + (endTime - startTime) + " milliseconds");
            //endTime = System.currentTimeMillis();
            //energyMapTime += (endTime - startTime);
            //System.out.print("energy map created...");

            //startTime = System.currentTimeMillis();
            int[] path = shortestPath(energyMap);
            //endTime = System.currentTimeMillis();
            //System.out.println("Pathfinding for " + i + " took " + (endTime - startTime) + " milliseconds");
            //shortestSeamTime += (endTime - startTime);
            //System.out.print("seam carved...");

            //startTime = System.currentTimeMillis();
            image = removePath(path, image);
            long endTime = System.currentTimeMillis();
            System.out.println("Iteration " + i + " took " + (endTime - startTime) + " milliseconds");
            //pathRemovalTime += (endTime - startTime);
            //System.out.println("path removed!");


        }
        /*
        System.out.println("For a new image:");
        System.out.println("Convert to RGB Time: " + convertToRGBTime + " ms");
        System.out.println("Energy mapping Time: " + energyMapTime + " ms");
        System.out.println("Path identification Time: " + shortestSeamTime + " ms");
        System.out.println("Path removal Time: " + pathRemovalTime + " ms");

         */
        File outputfile = new File("src/main/resources/images/carved.PNG");
        ImageIO.write(image, "PNG", outputfile);
        return 1;

    }

    // Takes a buffered image and converts into a 2d array with Color object for each pixel
    // TODO: Handle alpha channel
    private Color[][] convertToRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)  {
                result[x][y] = new Color(image.getRGB(x, y));
            }
        }
        return result;
    }

    private int[][] createEnergyMap(Color[][] colorArray) {
        int width = colorArray.length;
        int height = colorArray[0].length;
        int[][] energyArray = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)  {
                // Typed out for readability
                // Energy mapping algorithm is Δx^2(x, y) + Δy^2(x, y)
                Color prev = null;
                Color next = null;

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

                int deltaR = (int) Math.pow(prev.getRed() - next.getRed(), 2);
                int deltaG = (int) Math.pow(prev.getGreen() - next.getGreen(), 2);
                int deltaB = (int) Math.pow(prev.getBlue() - next.getBlue(), 2);

                int xDeltaSquare = deltaR + deltaG + deltaB;

                prev = null;
                next = null;
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

                deltaR = (int) Math.pow(prev.getRed() - next.getRed(), 2);
                deltaG = (int) Math.pow(prev.getGreen() - next.getGreen(), 2);
                deltaB = (int) Math.pow(prev.getBlue() - next.getBlue(), 2);

                int yDeltaSquare = deltaR + deltaG + deltaB;
                energyArray[x][y] = xDeltaSquare + yDeltaSquare;
            }
        }

        return energyArray;
    }

    //TODO: rename method
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

        // TODO: printing the paths, removing this debugging
        /*
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print("[" + distTo[x][y] + "]");
            }
            System.out.println();
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print("[" + parent[x][y] + "]");
            }
        }
        */
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

    private BufferedImage removePath(int[] path, BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = path[y]; x < image.getWidth() - 1; x++) {
                image.setRGB(x, y, image.getRGB(x+1, y));
            }
        }
        return image.getSubimage(0, 0, image.getWidth() - 1, image.getHeight());
    }
}

class PixelNode implements Comparable<PixelNode> {
    public int x;
    public int y;
    public int energy;

    public PixelNode(int x1, int y1, int e1) {
        x = x1;
        y = y1;
        energy = e1;
    }

    public int compareTo(PixelNode other) {
        return this.energy - other.energy;
    }
}
