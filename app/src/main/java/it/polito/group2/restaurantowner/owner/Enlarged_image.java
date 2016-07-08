package it.polito.group2.restaurantowner.owner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.polito.group2.restaurantowner.R;

public class Enlarged_image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enlarged_image);
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("photouri") != null) {
            String photoUri = getIntent().getExtras().getString("photouri");

            ImageView myImage = (ImageView) findViewById(R.id.enlarged_image);
            final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);

            progressBar.setVisibility(View.VISIBLE);
            Glide
                    .with(this)
                    .load(photoUri)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(myImage);
        }
    }

}
