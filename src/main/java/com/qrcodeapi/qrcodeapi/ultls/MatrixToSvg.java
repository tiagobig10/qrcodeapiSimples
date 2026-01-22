package com.qrcodeapi.qrcodeapi.ultls;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MatrixToSvg {
    private final int[][] matrix;

    private String imageSrc;

    public MatrixToSvg(int[][] matrix) {
        this.matrix = matrix;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getSvg(int dimension) {

        StringBuilder stringBuilder = new StringBuilder();

        int length = matrix.length;

        int size = dimension > 0 ? dimension : 256;

        int viewBoxSize = length + 2;

        int imageSize = viewBoxSize / 5;
        int imageXAndY = (viewBoxSize - imageSize) / 2;
        int imageSizeCalculated = imageSize + 2;
        int axisXAndY = imageXAndY - 2;

        String logoImage = imageSrc == null ? "" : imageSrc.isEmpty() ? "" : "<image xlink:href=" + imageSrc + " height=" + imageSize + " width=" + imageSize + " x=" + (imageXAndY - 1) + " y=" + (imageXAndY - 1) + " preserveAspectRatio=none></image>";


        for (var positionY = 0; positionY < length; positionY++) {

            for (int positionX = 0; positionX < length; positionX++) {

                var number = matrix[positionY][positionX];

                if (number > 0) {

                    if ((positionX < axisXAndY || imageSizeCalculated + axisXAndY <= positionX) || (positionY < axisXAndY || imageSizeCalculated + axisXAndY <= positionY) || logoImage.isEmpty()) {
                        stringBuilder.append("M").append(positionX).append(",").append(positionY).append("h1v1h-1v-1z");
                    }
                }
            }
        }

        // Build the SVG element as a string.
        String viewBox = "0 0 " + viewBoxSize + " " + viewBoxSize;
        String path = "<path transform=matrix(1,0,0,1,0,0) d=\"" + stringBuilder + "\" />";

        return "<svg  preserveAspectRatio=\"none\" viewBox=\"" + viewBox + "\" width=\"" + size +
                "\" height=\"" + size +
                "\" fill=\"" + "#000" +
                "\" shape-rendering=\"crispEdges\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">" +
                path + logoImage +
                "</svg>";
    }

    public String svg(int dimension) {
        StringBuilder stringBuilder = new StringBuilder();

        int length = matrix.length;

        for (int y = 0; y < length; y++) {

            for (int x = 0; x < length; x++) {

                int data = matrix[y][x];

                if (data == 1) {
                    stringBuilder.append(type1(x, y));
                }
                if (data == 2) {
                    stringBuilder.append(type1(x, y));
                }

                if (data > 2) {
                    stringBuilder.append(type6(x, y));
                }
            }
        }

        String string = stringBuilder.toString();

        return "<svg  width=" + dimension + " height= " + dimension + " viewBox=0,0," + (length) + "," + (length) + " >" +
                string +
                "</svg>";
    }


    private boolean image(int x, int y) {

        int length = matrix.length;

        //int size = dimension > 0 ? dimension : 256;

        int viewBoxSize = length + 2;

        int imageSize = viewBoxSize / 5;
        int imageXAndY = (viewBoxSize - imageSize) / 2;
        int imageSizeCalculated = imageSize + 2;
        int axisXAndY = imageXAndY - 2;

        //String logoImage = imageSrc == null ? "" : imageSrc.isEmpty() ? "" : "<image xlink:href=" + imageSrc + " height=" + imageSize + " width=" + imageSize + " x=" + (imageXAndY - 1) + " y=" + (imageXAndY - 1) + " preserveAspectRatio=none></image>";


        if ((x < axisXAndY || imageSizeCalculated + axisXAndY <= x) || (y < axisXAndY || imageSizeCalculated + axisXAndY <= y)) {
            return false;
        }

        return true;
    }

    private double random(double origin, double bound) {
        double value = new Random().nextDouble(origin, bound);
        return new BigDecimal(value)
                .setScale(2, RoundingMode.DOWN)
                .doubleValue();
    }


    private String type1(int x, int y) {
        return "<rect x=" + x + " y=" + y + " width=1 height=1 fill=black />";
    }

    private String type2(int x, int y) {
        var r = random(0.2, 0.5);

        return "<circle cx=" + x + " cy=" + y + " transform=translate(0.5,0.5) r=" + r + " fill=black />";
    }

    private String type3(int x, int y) {
        var r = new Random().nextDouble(0.5, 1);

        return "<rect x=" + x + " y=" + y + " width=" + r + " height= " + r + " fill=black />";
    }

    private String type4(int x, int y) {
        var r = random(0.10, 0.16);

        return "<g transform=\"translate(" + x + "," + y + ") scale(" + r + ")\">" +
                "<path d=\"M6,1.8C5.9,1,5.3,0.4,4.5,0.3C3.9,0.2,3.4,0.5,3,0.9C2.6,0.5,2.1,0.3,1.6,0.3C0.8,0.4,0.1,1,0,1.8 C0,2.3,0.1,2.7,0.3,3l0,0l0,0c0.1,0.1,0.2,0.2,0.3,0.3l1.9,2.2c0.3,0.3,0.7,0.3,0.9,0l1.8-1.9c0.1-0.1,0.3-0.3,0.4-0.5 C5.9,2.8,6.1,2.3,6,1.8z\"/>" +
                "</g>";
    }

    private String type5(int x, int y) {

        int wh = matrix.length / 5;

        return "<rect x=" + x + " y=" + y + "  width=" + wh + " height=" + wh + "\n" +
                "  style=\"fill:red;opacity:0.5\" />";
    }

    private String type6(int x, int y) {

        int size = 100;

        int border = size / 2;

        int topLeft = 20;
        int topRight = 0;
        int bottomRight = 0;
        int bottomLeft = 0;


        return "<g transform=\"translate(" + x + "," + y + ") scale(0.01)\">" +

                "<path " +
                "d=\"M " + topLeft + " 0 " +
                "L " + (size - topRight) + " 0 " +
                "A " + topRight + " " + topRight + " 0 0 1 " + size + " " + topRight + " " +
                "L " + size + " " + (size - bottomRight) + " " +
                "A " + bottomRight + " " + bottomRight + " 0 0 1 " + (size - bottomRight) + " " + size + " " +
                "L " + bottomLeft + " " + size + " " +
                "A " + bottomLeft + " " + bottomLeft + " 0 0 1 0 " + (size - bottomLeft) + " " +
                "L 0 " + topLeft + " " +
                "A " + topLeft + " " + topLeft + " 0 0 1 " + topLeft + " 0 " +
                "Z \"" +
                "fill=\"black\"  />" +

                "</g>";
    }


}
