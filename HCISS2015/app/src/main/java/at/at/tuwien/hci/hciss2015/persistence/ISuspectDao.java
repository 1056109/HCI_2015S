package at.at.tuwien.hci.hciss2015.persistence;

import java.util.List;

import at.at.tuwien.hci.hciss2015.domain.Suspect;

/**
 * Created by Michael on 18.06.2015.
 */
public interface ISuspectDao {

    List<Suspect> getAllSuspects();
    List<Suspect> getSuspectsByCharacterisitcs(String scar, String glasses, String skincolor, String haircolor, String beard);
    Suspect getSuspect(int id);
    long countSuspects();

}
