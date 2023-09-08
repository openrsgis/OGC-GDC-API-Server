package whu.edu.cn.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Provide some utility function.
 */
@Component
public class GeoUtil {

    /**
     * Converts a bounding box represented by four double values into a Well Known Text (WKT) polygon string.
     * The polygon string is of the form 'POLYGON((minX minY, minX maxY, maxX maxY, maxX minY, minX minY))'.
     *
     * @param minX the minimum x value of the bounding box
     * @param minY the minimum y value of the bounding box
     * @param maxX the maximum x value of the bounding box
     * @param maxY the maximum y value of the bounding box
     * @return a WKT polygon string representing the bounding box
     */
    public String DoubleToWKT(double minX, double minY, double maxX, double maxY) {
        return "POLYGON((" + minX + " " + minY + ", " + minX + " " + maxY + ", " + maxX + " " + maxY + ", " + maxX + " " + minY + "," + minX + " " + minY + "))";
    }

    /**
     * Converts eight string values representing the coordinates of an image into a Well Known Text (WKT) polygon string.
     * The polygon string is of the form 'POLYGON((TopLeftLongitude TopLeftLatitude, TopRightLongitude TopRightLatitude, BottomRightLongitude BottomRightLatitude, BottomLeftLongitude BottomLeftLatitude, TopLeftLongitude TopLeftLatitude))'.
     *
     * @param TopLeftLongitude     the longitude of the top-left corner of the image
     * @param TopLeftLatitude      the latitude of the top-left corner of the image
     * @param TopRightLongitude    the longitude of the top-right corner of the image
     * @param TopRightLatitude     the latitude of the top-right corner of the image
     * @param BottomRightLongitude the longitude of the bottom-right corner of the image
     * @param BottomRightLatitude  the latitude of the bottom-right corner of the image
     * @param BottomLeftLongitude  the longitude of the bottom-left corner of the image
     * @param BottomLeftLatitude   the latitude of the bottom-left corner of the image
     * @return a WKT polygon string representing the image
     */
    public String imageToWKT(String TopLeftLongitude, String TopLeftLatitude, String TopRightLongitude, String TopRightLatitude,
                             String BottomRightLongitude, String BottomRightLatitude, String BottomLeftLongitude, String BottomLeftLatitude) {
        return "POLYGON((" + TopLeftLongitude + " " + TopLeftLatitude + ", " + TopRightLongitude + " " + TopRightLatitude + ", "
                + BottomRightLongitude + " " + BottomRightLatitude + ", " + BottomLeftLongitude + " " + BottomLeftLatitude + ", "
                + TopLeftLongitude + " " + TopLeftLatitude + "))";
    }

    /**
     * Returns the href (URL) for the specified Coordinate Reference System (CRS) code.
     *
     * @param crsCode the code of the CRS
     * @return the href (URL) for the specified CRS code
     */
    public String getCRSHref(String crsCode) {
        if (crsCode.equals("EPSG:4326")) {
            return "http://www.opengis.net/def/crs/OGC/1.3/CRS84";
        } else {
            return null;
        }
    }
    /**
     * Compares two extents and returns the bigger one.
     * An extent is represented by a list of four double values (minX, minY, maxX, maxY).
     * The method creates a new list of four double values representing the bigger extent.
     * The first two values of the new list are the minimum x and y values of the two extents.
     * The last two values of the new list are the maximum x and y values of the two extents.
     * @param extent1 the first extent to compare
     * @param extent2 the second extent to compare
     * @return a list of four double values representing the bigger extent
     */
    public List<Double> compareExtentAndGetBigger(List<Double> extent1, List<Double> extent2) {
        List<Double> newExtent = new ArrayList<>();
        if ((extent2.get(0) < extent1.get(0))) {
            newExtent.add(0, extent2.get(0));
        } else {
            newExtent.add(0, extent1.get(0));
        }
        if ((extent2.get(1) < extent1.get(1))) {
            newExtent.add(1, extent2.get(1));
        } else {
            newExtent.add(1, extent1.get(1));
        }
        if ((extent2.get(2) > extent1.get(2))) {
            newExtent.add(2, extent2.get(2));
        } else {
            newExtent.add(2, extent1.get(2));
        }
        if ((extent2.get(3) > extent1.get(3))) {
            newExtent.add(3, extent2.get(3));
        } else {
            newExtent.add(3, extent1.get(3));
        }
        return newExtent;
    }

    /**
     * Checks if two extents are the same.
     * An extent is represented by a list of four double values (minX, minY, maxX, maxY).
     * The method returns true if the two extents have the same values for minX, minY, maxX, and maxY.
     * Otherwise, it returns false.
     * @param extent1 the first extent to compare
     * @param extent2 the second extent to compare
     * @return true if the two extents are the same, false otherwise
     */
    public Boolean isSameWithinExtents(List<Double> extent1, List<Double> extent2) {
        return extent1.get(0).equals(extent2.get(0)) && extent1.get(1).equals(extent2.get(1)) &&
                extent1.get(2).equals(extent2.get(2)) && extent1.get(3).equals(extent2.get(3));
    }

}
