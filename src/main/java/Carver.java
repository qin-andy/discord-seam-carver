import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Carver {

    //TODO: remove testing main statement
    public static void main(String[] args) throws IOException { // For testing
        Carver carver = new Carver();
        carver.carve("src/main/resources/images/lapp.png", 200);
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
        long iterationStartTime = System.currentTimeMillis();
        long interationEndTime;
        for (int i = 0; i < cutSize; i++) {
            //System.out.print("Working on seam " + i + "...");
            long startTime = System.currentTimeMillis();
            //Color[][] imageRGB = convertToRGB(image);
            int[][] imageRGB = convertToRGB2(image);
            long endTime = System.currentTimeMillis();
            //System.out.println("RGB Conversion for " + i + " took " + (endTime - startTime) + " milliseconds");
            convertToRGBTime += (endTime - startTime);
            //System.out.print("converted RGB...");

            startTime = System.currentTimeMillis();
            //int[][] energyMap = createEnergyMap(imageRGB);
            int[][] energyMap = createEnergyMap2(imageRGB);
            endTime = System.currentTimeMillis();
            //System.out.println("Energy mapping for " + i + " took " + (endTime - startTime) + " milliseconds");
            energyMapTime += (endTime - startTime);
            //System.out.print("energy map created...");

            startTime = System.currentTimeMillis();
            int[] path = shortestPath(energyMap);
            endTime = System.currentTimeMillis();
            //System.out.println("Pathfinding for " + i + " took " + (endTime - startTime) + " milliseconds");
            shortestSeamTime += (endTime - startTime);
            //System.out.print("seam carved...");

            startTime = System.currentTimeMillis();
            image = removePath(path, image);
            endTime = System.currentTimeMillis();
            //System.out.println("Path removal for " + i + " took " + (endTime - startTime) + " milliseconds");
            pathRemovalTime += (endTime - startTime);
            //System.out.println("path removed!");
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


        File outputFile = new File("src/main/resources/images/carved.PNG");
        ImageIO.write(image, "PNG", outputFile);
        return 1;

    }

    // Takes a buffered image and converts into a 2d array with Color object for each pixel
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

    private int[][] convertToRGB2(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[width][height];
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        int pixelLength = 3;
        if (hasAlphaChannel)
        {
            pixelLength = 4;
        }


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)  {
                int pos = (y * pixelLength * width) + (x * pixelLength);
                int argb = -16777216; // 255 alpha
                if (hasAlphaChannel)
                {
                    argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
                }

                argb += ((int) pixels[pos++] & 0xff); // blue
                argb += (((int) pixels[pos++] & 0xff) << 8); // green
                argb += (((int) pixels[pos++] & 0xff) << 16); // red
                result[x][y] = argb;
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

    private int[][] createEnergyMap2(int[][] colorArray) {
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


                int pB = prev & 0xff;
                int pG = (prev & 0xff00) >> 8;
                int pR = (prev & 0xff0000) >> 16;

                int nB = next & 0xff;
                int nG = (next & 0xff00) >> 8;
                int nR = (next & 0xff0000) >> 16;

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

                pB = prev & 0xff;
                pG = (prev & 0xff00) >> 8;
                pR = (prev & 0xff0000) >> 16;

                nB = next & 0xff;
                nG = (next & 0xff00) >> 8;
                nR = (next & 0xff0000) >> 16;

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

    private int[] shortestPath2(int[][] energyArray) {
        int width = energyArray.length;
        int height = energyArray[0].length;

        // Each [x][y] pair is modelled as a node
        int[][] parent = new int[width][height]; // The x value of the node's parent; the y is the child y - 1
        PixelNode[][] pixelNodes = new PixelNode[width][height];
        PriorityQueue<PixelNode> perimeter = new PriorityQueue<>();

        for (int i = 0; i < width; i++) {
            pixelNodes[i][0] = new PixelNode(i, 0, energyArray[i][0]);
            perimeter.add(pixelNodes[i][0]);
            // TODO: make sure comparable works for pixel nodes (queue is in order)
        }

        PixelNode currentNode = null;
        while (!perimeter.isEmpty()) {
            // Traverse through every node in order for a weighted path tree
            currentNode = perimeter.poll();
            //System.out.println(currentNode);
            if (currentNode.y == height - 1) {
                break;
            }

            int lower = -1;
            int upper = 1;
            if (currentNode.x == 0) {
                lower = 0;
            } else if (currentNode.x == width - 1) {
                upper = 0;
            }

            // For each of the current node's children, check if the path through the current node is shorter
            for (int i = lower; i < upper + 1; i++) {
                int x = currentNode.x;
                int y = currentNode.y;
                if (pixelNodes[x+i][y+1] == null) {
                    pixelNodes[x+i][y+1] = new PixelNode(x + i, y + 1, energyArray[x+i][y+1] + currentNode.energy);
                    perimeter.add(pixelNodes[x+i][y+1]);
                    parent[x+i][y+1] = x;
                }
            }
        }

        // Start the minPath array to store the x values of the minimum path, with the indicies indicating the y coord
        int[] minPath = new int[height];
        minPath[height-1] = currentNode.x;

        int childX = currentNode.x;
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

    public String toString() {
        return "[" + x + "," + y + "," + energy + "]";
    }
}

