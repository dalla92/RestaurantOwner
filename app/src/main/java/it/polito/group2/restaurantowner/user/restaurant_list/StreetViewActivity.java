package it.polito.group2.restaurantowner.user.restaurant_list;

/**
 * Created by Alessio on 28/05/2016.
 */
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import it.polito.group2.restaurantowner.R;

public class StreetViewActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private LatLng street_view_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_activity);

        Bundle b = this.getIntent().getExtras();
        if (b != null)
            street_view_position = b.getParcelable("STREET_VIEW_POSITION");

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setPosition(street_view_position);
    }

}
