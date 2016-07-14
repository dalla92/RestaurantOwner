package it.polito.group2.restaurantowner.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by TheChuck on 14/07/2016.
 */
public class PermissionUtil {

    public static void checkWritePermission(Activity activity, final int REQUEST_WRITE_STORAGE) {

        boolean hasPermission = (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    public static void checkLocationPermission(Activity activity, final int REQUEST_LOCATION) {

        boolean hasPermission = (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }
}
