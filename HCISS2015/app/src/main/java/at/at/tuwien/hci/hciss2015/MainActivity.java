package at.at.tuwien.hci.hciss2015;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * sources @
 * http://guides.cocoahero.com/google-maps-android-custom-tile-providers.html
 * https://developers.google.com/maps/documentation/android/tileoverlay
 */

public class MainActivity extends FragmentActivity {

    private GoogleMap map;

    /*
    private final LatLng VIENNA = new LatLng(48.209272, 16.372801);

    private static final int MIN_ZOOM = 12;

    private static final int MAX_ZOOM = 16;

    private static final String TILE_SERVER_URL = "http://tile.openstreetmap.org/";
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        //map.setMapType(GoogleMap.MAP_TYPE_NONE);

        TileOverlayOptions opts = new TileOverlayOptions();

        opts.tileProvider(new MapBoxOnlineTileProvider("mapbox.light"));

        opts.zIndex(5);

        TileOverlay overlay = map.addTileOverlay(opts);

        /*
        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                //String s = String.format(TILE_SERVER_URL, zoom, x, y);
                String s = TILE_SERVER_URL + String.valueOf(zoom) + "/" + String.valueOf(x) + "/" + String.valueOf(y) + ".png";

                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }

                System.out.println("URL: " + s);
                try {
                    return new URL(s);
                } catch (MalformedURLException ex) {
                    throw new AssertionError(ex);
                }
            }
        };

        TileOverlayOptions options = new TileOverlayOptions();
        options.visible(true);
        options.tileProvider(tileProvider);

        TileOverlay tileOverlay = mMap.addTileOverlay(options);

        System.out.println("is overlay visible: " + tileOverlay.isVisible());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VIENNA, 15));
        */
    }

    /*
    private boolean checkTileExists(int x, int y, int zoom) {
        if ((zoom < MIN_ZOOM || zoom > MAX_ZOOM)) {
            return false;
        }
        return true;
    }
    */

}
