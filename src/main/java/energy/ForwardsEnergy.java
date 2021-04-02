package energy;

public class ForwardsEnergy {

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
            }
        }
        return energyArray;
    }
}
