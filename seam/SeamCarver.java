import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */
public class SeamCarver {
    private static final double BORDER_ENERGY = 1000.0;
    private int[][] pictureColor;
    private double[][] energyMap;
    private int width, height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("argument is null");

        width = picture.width();
        height = picture.height();

        pictureColor = new int[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++)
                pictureColor[h][w] = picture.getRGB(w, h);
        }

        energyMap = new double[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                energyMap[h][w] = calEnergy(w, h);
            }
        }

    }

    // current picture
    public Picture picture() {
        Picture curPicture = new Picture(width, height);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++)
                curPicture.setRGB(w, h, pictureColor[h][w]);
        }
        return curPicture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    private boolean isMargin(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return true;
        return false;
    }

    private void validateRowIndex(int row) {
        if (row < 0 || row >= height)
            throw new IllegalArgumentException(
                    "row index must be between 0 and " + (height() - 1) + ": " + row);
    }

    private void validateColumnIndex(int col) {
        if (col < 0 || col >= width)
            throw new IllegalArgumentException(
                    "column index must be between 0 and " + (width() - 1) + ": " + col);
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);
        return energyMap[y][x];
    }

    private double calEnergy(int x, int y) {
        if (isMargin(x, y)) return BORDER_ENERGY;
        // x is column and y is row

        int leftRGB = pictureColor[y][x - 1];
        int rigRGB = pictureColor[y][x + 1];
        int upRGB = pictureColor[y - 1][x];
        int downRGB = pictureColor[y + 1][x];

        return Math.sqrt(delta(leftRGB, rigRGB) + delta(upRGB, downRGB));
    }

    private double delta(int x, int y) {
        int xr = (x >> 16) & 0xFF;
        int yr = (y >> 16) & 0xFF;
        int xg = (x >> 8) & 0xFF;
        int yg = (y >> 8) & 0xFF;
        int xb = (x) & 0xFF;
        int yb = (y) & 0xFF;
        return Math.pow(xr - yr, 2) + Math.pow(xg - yg, 2) + Math.pow(xb - yb, 2);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (height == 0) {
            int[] emptySeam = new int[0];
            return emptySeam;
        }

        double[][] transEnergyMap = new double[width][height];
        for (int h = 0; h < width; h++) {
            for (int w = 0; w < height; w++) {
                transEnergyMap[h][w] = energyMap[w][h];
            }
        }
        int[] horizontalSeam = findVerticalSeam(transEnergyMap, width, height);
        return horizontalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (width == 0) {
            int[] emptySeam = new int[0];
            return emptySeam;
        }
        return findVerticalSeam(energyMap, height, width);
    }

    private int[] findVerticalSeam(double[][] localEnergyMap, int curHeight, int curWidth) {

        int[] verticalSeam = new int[curHeight];
        double[][] distTo = new double[curHeight][curWidth];
        int[][] verticalEdgeTo = new int[curHeight][curWidth];

        if (curWidth == 1) {
            Arrays.fill(verticalSeam, 0);
            return verticalSeam;
        }

        for (int h = 0; h < curHeight; h++)
            for (int w = 0; w < curWidth; w++) {
                if (h == 0) distTo[h][w] = 0;
                else distTo[h][w] = Double.POSITIVE_INFINITY;
            }

        for (int h = 0; h < curHeight - 1; h++) {
            for (int w = 0; w < curWidth; w++) {
                if (w == 0) {
                    relaxVertical(h, w, h + 1, w, localEnergyMap, distTo, verticalEdgeTo);
                    relaxVertical(h, w, h + 1, w + 1, localEnergyMap, distTo, verticalEdgeTo);
                }
                else if (w == curWidth - 1) {
                    relaxVertical(h, w, h + 1, w - 1, localEnergyMap, distTo, verticalEdgeTo);
                    relaxVertical(h, w, h + 1, w, localEnergyMap, distTo, verticalEdgeTo);
                }
                else {
                    relaxVertical(h, w, h + 1, w - 1, localEnergyMap, distTo, verticalEdgeTo);
                    relaxVertical(h, w, h + 1, w, localEnergyMap, distTo, verticalEdgeTo);
                    relaxVertical(h, w, h + 1, w + 1, localEnergyMap, distTo, verticalEdgeTo);
                }
            }
        }

        int minDistIndex = 0;
        double minDist = Double.POSITIVE_INFINITY;
        int lastH = curHeight - 1;
        for (int w = 0; w < curWidth; w++) {
            if (distTo[lastH][w] < minDist) {
                minDist = distTo[lastH][w];
                minDistIndex = w;
            }
        }

        for (int h = lastH; h >= 0; h--) {
            verticalSeam[h] = minDistIndex;
            minDistIndex = verticalEdgeTo[h][minDistIndex];
        }

        return verticalSeam;
    }

    private void relaxVertical(int vx, int vy, int wx, int wy, double[][] localEnergyMap,
                               double[][] distTo, int[][] verticalEdgeTo) {
        // horizontal use transposed picture, and then call vertical, then transposed again
        // so there is no relax method in horizontal version
        if (distTo[wx][wy] > distTo[vx][vy] + localEnergyMap[wx][wy]) {
            distTo[wx][wy] = distTo[vx][vy] + localEnergyMap[wx][wy];
            verticalEdgeTo[wx][wy] = vy;
        }
    }

    private void transpose() {
        int[][] transColor = new int[width][height];
        for (int h = 0; h < width; h++) {
            for (int w = 0; w < height; w++) {
                transColor[h][w] = pictureColor[w][h];
            }
        }

        double[][] transEnergyMap = new double[width][height];
        for (int h = 0; h < width; h++) {
            for (int w = 0; w < height; w++) {
                transEnergyMap[h][w] = energyMap[w][h];
            }
        }

        int temp = width;
        width = height;
        height = temp;

        pictureColor = transColor;
        energyMap = transEnergyMap;
    }

    private void validVerticalSeam(int[] seam) {
        if ((seam == null) || (seam.length != height))
            throw new IllegalArgumentException("argument is invalid");
        validateColumnIndex(seam[0]);
        for (int i = 1; i < seam.length; i++) {
            validateColumnIndex(seam[i]);
            if (Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException("argument is invalid");
        }
    }

    private void validHorizontalSeam(int[] seam) {
        if ((seam == null) || (seam.length != width))
            throw new IllegalArgumentException("argument is invalid");
        validateRowIndex(seam[0]);
        for (int i = 1; i < seam.length; i++) {
            validateRowIndex(seam[i]);
            if (Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException("argument is invalid");
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validHorizontalSeam(seam);
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validVerticalSeam(seam);
        width--;
        for (int h = 0; h < height; h++) {
            if (seam[h] < width) {
                for (int w = seam[h]; w < width; w++) {
                    pictureColor[h][w] = pictureColor[h][w + 1];
                    energyMap[h][w] = energyMap[h][w + 1];
                }
            }
        }

        for (int h = 0; h < height; h++) {
            if (seam[h] == 0) {
                energyMap[h][0] = BORDER_ENERGY;
            }
            else if (seam[h] == width) {
                energyMap[h][width - 1] = BORDER_ENERGY;
            }
            else {
                energyMap[h][seam[h]] = calEnergy(seam[h], h);
                energyMap[h][seam[h] - 1] = calEnergy(seam[h] - 1, h);
            }
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture("./3x4.png");
        StdOut.printf("image is %d columns by %d rows\n", picture.width(), picture.height());
        picture.show();
        SeamCarver sc = new SeamCarver(picture);
        for (int i : sc.findVerticalSeam())
            StdOut.print(i + " -> ");
        StdOut.println();
        for (int i : sc.findHorizontalSeam())
            StdOut.print(i + " -> ");
        StdOut.println();

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%.9f ", sc.energy(col, row));
            StdOut.println();
        }
    }

}
