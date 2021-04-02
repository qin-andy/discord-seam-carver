package energy;

public class ForwardsEnergy implements EnergyStrategy{
    public static final int MAX_ENERGY = 390150; // Maximum energy value for any pixel
    // Builds an energy map based on the "Forwards Energy" described in the Shamir Avidan Rubinstein paper
    // For use with Forwards Energy Pathfinder
    public int[] calculateEnergy(int[] ARGBValues, int width, int height) {
        int[] energyArray = new int[3*width*height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)  {
                // Typed out for readability
                // Energy mapping algorithm is Δx^2(x, y) + Δy^2(x, y)
                int colorRight;
                int colorLeft;
                int colorDown;
                int colorUp;

                // Edges are trated as adjacent to the opposite side
                if (x == 0) {
                    colorLeft = ARGBValues[y*width + (width-1)];
                    colorRight = ARGBValues[y*width + x+1];
                } else if (x == width - 1) {
                    colorLeft = ARGBValues[y*width + x-1];
                    colorRight = ARGBValues[y*width];
                } else {
                    colorLeft = ARGBValues[y*width + x-1];
                    colorRight = ARGBValues[y*width + x+1];
                }

                int leftB = colorLeft & 0xff;
                int leftG = (colorLeft & 0xff00) << 8;
                int leftR = (colorLeft & 0xff0000) << 16;

                int rightB = colorRight & 0xff;
                int rightG = (colorRight & 0xff00) >> 8;
                int rightR = (colorRight & 0xff0000) >> 16;

                int deltaR = (int) Math.pow(leftR - rightR, 2);
                int deltaG = (int) Math.pow(leftG - rightG, 2);
                int deltaB = (int) Math.pow(leftB - rightB, 2);

                int xDeltaSquare = deltaR + deltaG + deltaB;

                if (y == 0) {
                    colorUp = ARGBValues[(height-1)*width + x];
                    colorDown = ARGBValues[width*(y+1) + x];
                } else if (y == height - 1) {
                    colorUp = ARGBValues[(y-1)*width + x];
                    colorDown = ARGBValues[x];
                } else {
                    colorUp = ARGBValues[(y-1)*width + x];
                    colorDown = ARGBValues[(y+1)*width + x];
                }

                int upB = colorUp & 0xff;
                int upG = (colorUp & 0xff00) >> 8;
                int upR = (colorUp & 0xff0000) >> 16;

                int downB = colorDown & 0xff;
                int downG = (colorDown & 0xff00) >> 8;
                int downR = (colorDown & 0xff0000) >> 16;

                deltaR = (int) Math.pow(upR - downR, 2);
                deltaG = (int) Math.pow(upG - downG, 2);
                deltaB = (int) Math.pow(upB - downB, 2);

                int yDeltaSquare = deltaR + deltaG + deltaB;

                int baseEnergy = xDeltaSquare + yDeltaSquare;

                energyArray[3*y*width + 3*x] = -1;
                int forwardsCost;

                // Entering from Upper Right Energy, left and down will be adjacent
                if (x == 0) {
                    energyArray[3*y*width + 3*x] = MAX_ENERGY;
                } else {
                    forwardsCost = colorDifference(colorRight, colorUp) + xDeltaSquare;
                    energyArray[3*y*width + 3*x] = forwardsCost + baseEnergy;
                }

                // Entering from Upper Energy
                energyArray[3*y*width + 3*x + 1] = baseEnergy + xDeltaSquare;

                // Entering from Upper Left Path Energy, right and down will be adjacent
                if (x == width - 1) {
                    energyArray[3*y*width + 3*x + 2] = MAX_ENERGY;
                } else {
                    forwardsCost = colorDifference(colorLeft, colorUp) + xDeltaSquare;
                    energyArray[3*y*width + 3*x + 2] = forwardsCost + baseEnergy;
                }
            }
        }
        return energyArray;
    }

    // Takes two RGB int values and calculates the color difference between them
    private int colorDifference(int a, int b) {
        int aB = a & 0xff;
        int aG = (a & 0xff00) << 8;
        int aR = (a & 0xff0000) << 16;

        int bB = b & 0xff;
        int bG = (b & 0xff00) >> 8;
        int bR = (b & 0xff0000) >> 16;

        int deltaR = (int) Math.pow(aR - bR, 2);
        int deltaG = (int) Math.pow(aG - bG, 2);
        int deltaB = (int) Math.pow(aB - bB, 2);
        return deltaR + deltaG + deltaB;
    }
}
