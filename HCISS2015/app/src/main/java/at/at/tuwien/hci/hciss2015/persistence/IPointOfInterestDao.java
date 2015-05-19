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
    long countPOIs();
    int updatePOIFlag(int id, int flag);
    int updatePOI(PointOfInterest poi);
    int resetAllFlags();

}
