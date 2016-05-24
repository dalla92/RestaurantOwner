package it.polito.group2.restaurantowner.gallery;

/**
 * Created by TheChuck on 09/05/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

public class FullScreenGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_gallery);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        ArrayList<String> mGridData = i.getStringArrayListExtra("urls");

        for(String url: mGridData){
            Log.d("fullscreen", url);
        }

        ExtendedViewPager viewPager = (ExtendedViewPager) findViewById(R.id.pager);
        FullScreenGalleryAdapter adapter = new FullScreenGalleryAdapter(this, mGridData);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

}
