package tsingjyujing.geo.basic.geounit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class GeometryPoint<T> implements java.io.Serializable {
    public T getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(T userInfo) {
        this.userInfo = userInfo;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    protected T userInfo = null;
    protected double longitude = 0.0D;
    protected double latitude = 0.0D;

    /**
     * Radius of earth: km
     */
    public static final double EARTH_RADIUS = 6378.5;

    /**
     * Multiplier transfer degree to rad
     */
    public static final double DEG2RAD = Math.PI / 180;

    /**
     * Multiplier transfer rad to degree
     */
    public static final double RAD2DEG = 180 / Math.PI;

    /**
     * 2^30
     */
    public static final long POW2E30 = 0x40000000L;

    /**
     * 2^31
     */
    public static final long POW2E31 = 0x80000000L;

    private static final double MAX_INNER_PRODUCT_FOR_UNIT_VECTOR = 1.0D;

    /**
     * @param lng longitude
     * @param lat latitude
     */
    public GeometryPoint(double lng, double lat) {
        longitude = lng;
        latitude = lat;
    }

    /**
     * @param lng           longitude
     * @param lat           latitude
     * @param userInfoInput Input tag
     */
    public GeometryPoint(double lng, double lat, T userInfoInput) {
        longitude = lng;
        latitude = lat;
        userInfo = userInfoInput;
    }


    public double[] get3DPos() {
        return get3DPos(1.0);
    }

    public double[] get3DPos(double radius) {
        double cosLatitude = Math.cos(latitude * DEG2RAD);
        double[] euclidPosition = {
                Math.cos(longitude * DEG2RAD) * cosLatitude * radius,
                Math.sin(longitude * DEG2RAD) * cosLatitude * radius,
                Math.sin(latitude * DEG2RAD) * radius
        };
        return euclidPosition;
    }

    /**
     * @param accuracy
     * @return HashCode
     */
    public long geoHashCode(long accuracy) {
        accuracy = Math.min(accuracy, POW2E30);
        long lngCode = (long) Math.floor((longitude + 180.0D) / 180 * accuracy);
        long latCode = (long) Math.floor((latitude + 90.00D) / 180 * accuracy);
        return lngCode * POW2E31 + latCode;
    }

    /**
     * Get the boundary of given Geometry Hash Block (given by Long) and point
     *
     * @param blockHash   given block hash
     * @param accuracy    accuracy of hash block
     * @param centerPoint search point opp to this block
     * @return List of the points seems to nearest or farthest
     */
    public List<GeometryPoint> geometryHashBlockBoundary(long blockHash, long accuracy, GeometryPoint centerPoint) {
        List<GeometryPoint> returnPoints = new ArrayList<GeometryPoint>();
        long latCode = blockHash % POW2E31;
        long lngCode = (blockHash - latCode) / POW2E31;
        double lngMin = (180.0D * lngCode - 180.0D * accuracy) / ((double) accuracy);
        double latMin = (180.0D * latCode - 90.00D * accuracy) / ((double) accuracy);
        double lngMax = (180.0D * (lngCode + 1) - 180.0D * accuracy) / ((double) accuracy);
        double latMax = (180.0D * (latCode + 1) - 90.00D * accuracy) / ((double) accuracy);
        returnPoints.add(new GeometryPoint(lngMin, latMin));
        returnPoints.add(new GeometryPoint(lngMin, latMax));
        returnPoints.add(new GeometryPoint(lngMax, latMin));
        returnPoints.add(new GeometryPoint(lngMax, latMax));

        if (lngMin < centerPoint.longitude && lngMax > centerPoint.longitude) {
            returnPoints.add(new GeometryPoint(centerPoint.longitude, latMin));
            returnPoints.add(new GeometryPoint(centerPoint.longitude, latMax));
        }

        double[] hashBoundary = new double[2];
        hashBoundary[0] = Math.cos((lngMin - centerPoint.longitude) * DEG2RAD);
        hashBoundary[1] = Math.cos((lngMax - centerPoint.longitude) * DEG2RAD);
        double upBoundary = Math.max(hashBoundary[0], hashBoundary[1]);
        double downBoundary = Math.min(hashBoundary[0], hashBoundary[1]);
        double partialDiffVariable = Math.tan(centerPoint.latitude * DEG2RAD);
        double judgeMinLatitude = partialDiffVariable / Math.tan(latMin * DEG2RAD);
        double judgeMaxLatitude = partialDiffVariable / Math.tan(latMax * DEG2RAD);
        if (judgeMinLatitude < upBoundary && judgeMinLatitude > downBoundary) {
            returnPoints.add(
                    new GeometryPoint(
                            centerPoint.longitude + Math.acos(judgeMinLatitude) * RAD2DEG,
                            judgeMinLatitude));
        }
        if (judgeMaxLatitude < upBoundary && judgeMaxLatitude > downBoundary) {
            returnPoints.add(
                    new GeometryPoint(
                            centerPoint.longitude + Math.acos(judgeMaxLatitude) * RAD2DEG,
                            judgeMinLatitude));
        }
        return returnPoints;
    }

    /**
     * @param accuracy
     * @return the bound point of this hash
     */
    public List<GeometryPoint> geometryHashBlockBoundary(long accuracy) {
        return geometryHashBlockBoundary(geoHashCode(accuracy), accuracy, this);
    }

    /**
     * @param points
     * @param centerPoint
     * @return
     */
    public double minDistance(Iterable<GeometryPoint> points, GeometryPoint centerPoint) {
        double minValue = EARTH_RADIUS * Math.PI;
        for (GeometryPoint point : points) {
            double dist = centerPoint.distance(point);
            if (minValue > dist) {
                minValue = dist;
            }
        }
        return minValue;
    }

    /**
     * @param points
     * @return
     */
    public double minDistance(List<GeometryPoint> points) {
        return minDistance(points, this);
    }

    /**
     * @param points
     * @param centerPoint
     * @return
     */
    public double maxDistance(List<GeometryPoint> points, GeometryPoint centerPoint) {
        double maxValue = 0.0D;
        for (GeometryPoint point : points) {
            double dist = point.distance(centerPoint);
            if (maxValue < dist) {
                maxValue = dist;
            }
        }
        return maxValue;
    }

    /**
     * @param points
     * @return
     */
    public double maxDistance(List<GeometryPoint> points) {
        return maxDistance(points, this);
    }

    /**
     * @param point1
     * @return distance(km) to this point
     */
    public double distance(GeometryPoint point1) {
        return geodesicDistance(point1, this);
    }

    /**
     * @param point1
     * @param point2
     * @return return distance between point1 and point2
     */
    public double distance(GeometryPoint point1, GeometryPoint point2) {
        return geodesicDistance(point1, point2);
    }


    private static double geodesicDistance(GeometryPoint point1, GeometryPoint point2) {
        double alpha = getInnerProduct(point1, point2);
        if (alpha >= MAX_INNER_PRODUCT_FOR_UNIT_VECTOR) {
            return 0.0D;
        } else if (alpha <= -MAX_INNER_PRODUCT_FOR_UNIT_VECTOR) {
            return Math.PI * EARTH_RADIUS;
        } else {
            return Math.acos(alpha) * EARTH_RADIUS;
        }
    }

    /**
     * @param point1
     * @param point2
     * @return 3D-vector inner product of two normlized vectors
     */
    public static double getInnerProduct(GeometryPoint point1, GeometryPoint point2) {
        return Math.sin(point1.latitude * DEG2RAD) * Math.sin(point2.latitude * DEG2RAD) +
                Math.cos(point1.latitude * DEG2RAD) * Math.cos(point2.latitude * DEG2RAD) *
                        Math.cos(point1.longitude * DEG2RAD - point2.longitude * DEG2RAD);
    }

    /**
     * @param point1
     * @return 3D-vector inner product of normlized vector and this vector
     */
    public double getInnerProduct(GeometryPoint point1) {
        return getInnerProduct(point1, this);
    }

    /**
     * @param point1
     * @param point2
     * @return another distance valuation
     */
    public double getInnerProductDistance(GeometryPoint point1, GeometryPoint point2) {
        return Math.PI - getInnerProduct(point1, point2);
    }

    /**
     * @param point1
     * @return another distance valuation
     */
    public double getInnerProductDistance(GeometryPoint point1) {
        return Math.PI - getInnerProduct(point1);
    }

    /**
     * @param pointList
     * @param centerPoint
     * @return get min inner product between list and center
     */
    public double minInnerProduct(List<GeometryPoint> pointList, GeometryPoint centerPoint) {
        double minInnerProduct = 1.0D;
        double innerProduct;
        for (GeometryPoint point : pointList) {
            innerProduct = point.getInnerProduct(centerPoint);
            if (minInnerProduct > innerProduct) {
                minInnerProduct = innerProduct;
            }
        }
        return minInnerProduct;
    }

    /**
     * @param pointList
     * @return get min inner productor between list and this
     */
    public double minInnerProduct(List<GeometryPoint> pointList) {
        return GeometryPoint.this.minInnerProduct(pointList, this);
    }

    /**
     * @param pointList
     * @param centerPoint
     * @return get max inner productor between list and center
     */
    public double maxInnerProduct(List<GeometryPoint> pointList, GeometryPoint centerPoint) {
        double maxInnerProduct = 0.0D;
        for (int i = 0; i < pointList.size(); ++i) {
            double ip = pointList.get(i).getInnerProduct(centerPoint);
            if (maxInnerProduct < ip) {
                maxInnerProduct = ip;
            }
        }
        return maxInnerProduct;
    }

    /**
     * @param pointList
     * @return get max inner productor between list and this
     */
    public double maxInnerProduct(List<GeometryPoint> pointList) {
        return GeometryPoint.this.maxInnerProduct(pointList, this);
    }

    /**
     * @param pointList
     * @param centerPoint
     * @return find nearest point
     */
    private GeometryPoint<T> nearestPoint(List<GeometryPoint<T>> pointList, GeometryPoint centerPoint) {
        int returnValue = 0;
        double maxInnerProduct = centerPoint.getInnerProduct(pointList.get(0));
        for (int i = 1; i < pointList.size(); ++i) {
            double innerProduct = centerPoint.getInnerProduct(pointList.get(i));
            if (innerProduct > maxInnerProduct) {
                maxInnerProduct = innerProduct;
                returnValue = i;
            }
        }
        return pointList.get(returnValue);
    }

    /**
     * @param points
     * @return find nearest point
     */
    public GeometryPoint<T> nearestPoint(List<GeometryPoint<T>> points) {
        return nearestPoint(points, this);
    }

    /**
     * @param points
     * @param centerPoint
     * @return find nearest point index
     */
    public int nearestPointIndex(List<GeometryPoint> points, GeometryPoint centerPoint) {
        int returnValue = 0;
        double maxInnerProduct = centerPoint.getInnerProduct(points.get(0));
        for (int i = 1; i < points.size(); ++i) {
            double innerProduct = centerPoint.getInnerProduct(points.get(i));
            if (innerProduct > maxInnerProduct) {
                maxInnerProduct = innerProduct;
                returnValue = i;
            }
        }
        return returnValue;
    }

    /**
     * @param points
     * @return find nearest point index
     */
    public int nearestPointIndex(List<GeometryPoint> points) {
        return nearestPointIndex(points, this);
    }

}
