package at.at.tuwien.hci.hciss2015.persistence;

import java.util.List;

import at.at.tuwien.hci.hciss2015.domain.PointOfInterest;

/**
 * Created by amsalk on 19.5.2015.
 */
public interface IPointOfInterestDao {

    PointOfInterest getSinglePOI(int id);
    List<PointOfInterest> getPOIsByType(int type);
    List<PointOfInterest> getAllVisitedPOIs();
    List<PointOfInterest> getAllUnvisitedPOIs();
    List<PointOfInterest> getAllPOIs();
    List<PointOfInterest> getPOIsByArea(int areaSize);
    List<PointOfInterest> getPOIsByPosition(double latitude, double longitude, int radius);
    List<PointOfInterest> getVisitedPOIsByPosition(double latitude, double longitude, int radius);
    List<PointOfInterest> getUnvisitedPOIsByPosition(double latitude, double longitude, int radius);
    List<PointOfInterest> getPOIsByPositionType(double latitude, double longitude, int radius, int type);
    List<PointOfInterest> getPOIsByMinMaxPosition(double latitude, double longitude, int minRadius, int maxRadius);
    List<PointOfInterest> getVisitedPOIsByMinMaxPosition(double latitude, double longitude, int minRadius, int maxRadius);
    List<PointOfInterest> getUnvisitedPOIsByMinMaxPosition(double latitude, double longitude, int minRadius, int maxRadius);
    List<PointOfInterest> getPOIsByMinMaxPositionType(double latitude, double longitude, int minRadius, int maxRadius, int type);
    long countPOIs();
    int updatePOIFlag(int id, int flag);
    int updatePOI(PointOfInterest poi);
    int resetAllFlags();

}
