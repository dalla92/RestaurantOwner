package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import it.polito.group2.restaurantowner.R;

public class UserRestaurantActivity extends AppCompatActivity {

    private String userID, restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing_user_restaurant);
        collapsing.setTitle("Restaurant Name");
        collapsing.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        ImageView image = (ImageView) findViewById(R.id.user_restaurant_image);
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = ContextCompat.getColor(UserRestaurantActivity.this, R.color.colorPrimaryDark);
                int primary =ContextCompat.getColor(UserRestaurantActivity.this, R.color.colorPrimary);

                collapsing.setContentScrimColor(palette.getMutedColor(primary));
                collapsing.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.bookmark_fab);
        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf( ContextCompat.getColor(this, android.R.color.white)));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isBookmark()) {
                        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_on_24dp));
                    }
                    else{
                        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this,R.drawable.ic_star_off_24dp));
                    }
                }
            });
        }
    }

    private boolean isBookmark(){
        return false;
    }
}
