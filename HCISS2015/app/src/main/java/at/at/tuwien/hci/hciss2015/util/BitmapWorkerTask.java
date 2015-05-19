package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import at.at.tuwien.hci.hciss2015.R;

/**
 * Created by nn on 19.5.2015.
 */
public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    private final Context context;

    public BitmapWorkerTask(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.hospital);

        return bitmap;
    }
}
