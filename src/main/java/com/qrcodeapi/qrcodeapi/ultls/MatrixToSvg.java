package com.qrcodeapi.qrcodeapi.ultls;

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
        int imageXAndY = (viewBoxSize - imageSize) / 2 ;
        int imageSizeCalculated = imageSize + 2;
        int axisXAndY = imageXAndY - 2;

        String logoImage = imageSrc == null ? "" : imageSrc.isEmpty() ? "" : "<image xlink:href=" + imageSrc + " height=" + imageSize + " width=" + imageSize + " x=" + (imageXAndY -1) + " y=" + (imageXAndY -1) + " preserveAspectRatio=none></image>";


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


}
