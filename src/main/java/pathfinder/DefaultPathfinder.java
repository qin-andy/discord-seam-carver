package pathfinder;

import java.util.Arrays;

// The default pathfinder for BackwardsEnergy strategy
public class DefaultPathfinder implements PathfinderStrategy {
    public int[] shortestPath(int energy[], int width, int height) {
        // Each [x][y] pair is modelled as a node
        int[] parent = new int[width*height]; // The x value of the node's parent; the y is the child y - 1
        int[] distTo = new int[width*height]; // The shortest distance to the node
        Arrays.fill(distTo, Integer.MAX_VALUE); // Initialize all distances to infinity
        for (int i = 0; i < width; i++) { // Reinitialize the first row elements as their respective energy
            distTo[i] = energy[i];
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
                    newDist = distTo[y*width + x] + energy[(y+1)*width + x+i];
                    if (newDist < distTo[(y+1)*width+x+i]) { //if the path through the current node is shorter
                        distTo[(y+1)*width+x+i] = newDist; // update the node with the newest shortest path
                        parent[(y+1)*width+x+i] = x; // store the newest shortest path in the parent
                    }
                }
            }
        }

        // Backtracking from the minimum of the last row:
        // Finding minimum of the last row:
        int minEnergy = distTo[(height-1)*width];
        int minX = 0;

        for (int x = 1; x < width; x++) { // Find the lowest energy path by looking at the bottom row of nodes
            if (distTo[(height-1)*width+x] < minEnergy) {
                minX = x;
                minEnergy = distTo[(height-1)*width+x];
            }
        }
        int[] minPath = new int[height];
        minPath[height-1] = minX;
        int childX = minX;

        int parentX;
        for (int y = height - 1; y > 0; y--) { // use the parent array to traverse backwards to find shortest path
            parentX = parent[y*width+childX];
            minPath[y-1] = parentX;
            childX = parentX;
        }
        return minPath;
    }
}
