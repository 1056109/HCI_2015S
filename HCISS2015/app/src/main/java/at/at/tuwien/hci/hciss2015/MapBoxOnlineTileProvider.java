package at.at.tuwien.hci.hciss2015;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * source @ http://guides.cocoahero.com/google-maps-android-custom-tile-providers.html
 */
public class MapBoxOnlineTileProvider extends UrlTileProvider {

    private static final String FORMAT;

    static {
        FORMAT = "http://api.tiles.mapbox.com/v3/%s/%d/%d/%d.png?access_token=sk.eyJ1IjoiYW1zYWxrIiwiYSI6InVuZVMzZVEifQ.FPEPb7EaHuL90ImmyTB_Gg";

    }

    private String mMapIdentifier;

    public MapBoxOnlineTileProvider(String mMapIdentifier) {
        super(256, 256);

        this.mMapIdentifier = mMapIdentifier;
    }

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
            URL url = new URL(String.format(FORMAT, mMapIdentifier, z, x, y));

            //debug
            System.out.println("URL: " + url);
            return url;
        }
        catch (MalformedURLException ex) {
            return null;
        }
    }

}
