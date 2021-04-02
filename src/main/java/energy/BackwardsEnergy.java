package energy;

public class BackwardsEnergy {

    // Builds an energy map based on the "backwards energy" based on the image's color gradient
    public int[] calculateEnergy(int[] ARGBValues, int width, int height) {
        int[] energyArray = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Typed out for readability
                // Energy mapping algorithm is Δx^2(x, y) + Δy^2(x, y)
                int prev;
                int next;

                // Edges are trated as adjacent to the opposite side
                if (x == 0) {
                    prev = ARGBValues[y * width + (width - 1)];
                    next = ARGBValues[y * width + x + 1];
                } else if (x == width - 1) {
                    prev = ARGBValues[y * width + x - 1];
                    next = ARGBValues[y * width];
                } else {
                    prev = ARGBValues[y * width + x - 1];
                    next = ARGBValues[y * width + x + 1];
                }

                int pB = prev & 0xFF;
                int pG = (prev & 0xFF) << 8;
                int pR = (prev & 0xFF) << 16;

                int nB = next & 0xff;
                int nG = (next & 0xff00) >> 8;
                int nR = (next & 0xff0000) >> 16;

                int deltaR = (int) Math.pow(pR - nR, 2);
                int deltaG = (int) Math.pow(pG - nG, 2);
                int deltaB = (int) Math.pow(pB - nB, 2);

                int xDeltaSquare = deltaR + deltaG + deltaB;

                if (y == 0) {
                    prev = ARGBValues[(height - 1) * width + x];
                    next = ARGBValues[width * (y + 1) + x];
                } else if (y == height - 1) {
                    prev = ARGBValues[(y - 1) * width + x];
                    next = ARGBValues[x];
                } else {
                    prev = ARGBValues[(y - 1) * width + x];
                    next = ARGBValues[(y + 1) * width + x];
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
                energyArray[y * width + x] = xDeltaSquare + yDeltaSquare;
            }
        }
        return energyArray;
    }
}
