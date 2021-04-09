package energy;

public class BackwardsEnergy implements EnergyStrategy {

    // Builds an energy map based on the "backwards energy" based on the image's color gradient
    public int[] calculateEnergy(int[] ARGBValues, int width, int height) {
        int[] energyArray = new int[width*height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Typed out for readability
                // Energy mapping algorithm is Δx^2(x, y) + Δy^2(x, y)
                int prev;
                int next;

                // Edges are trated as adjacent to the opposite side
                if (x == 0) {
                    prev = ARGBValues[y*width + (width-1)];
                    next = ARGBValues[y*width + x+1];
                } else if (x == width - 1) {
                    prev = ARGBValues[y*width + x-1];
                    next = ARGBValues[y*width];
                } else {
                    prev = ARGBValues[y*width + x-1];
                    next = ARGBValues[y*width + x+1];
                }
                int xDeltaSquare = colorDifference(prev, next);

                if (y == 0) {
                    prev = ARGBValues[(height-1)*width + x];
                    next = ARGBValues[(y+1)*width + x];
                } else if (y == height-1) {
                    prev = ARGBValues[(y-1) * width + x];
                    next = ARGBValues[x];
                } else {
                    prev = ARGBValues[(y-1) * width + x];
                    next = ARGBValues[(y+1) * width + x];
                }

                int yDeltaSquare = colorDifference(prev, next);
                energyArray[y * width + x] = xDeltaSquare + yDeltaSquare;
            }
        }
        return energyArray;
    }

    private int colorDifference(int a, int b) {
        int aB = a & 0xff;
        int aG = (a & 0xff00) >> 8;
        int aR = (a & 0xff0000) >> 16;

        int bB = b & 0xff;
        int bG = (b & 0xff00) >> 8;
        int bR = (b & 0xff0000) >> 16;

        int deltaR = (int) Math.pow(aR - bR, 2);
        int deltaG = (int) Math.pow(aG - bG, 2);
        int deltaB = (int) Math.pow(aB - bB, 2);
        return deltaR + deltaG + deltaB;
    }
}
