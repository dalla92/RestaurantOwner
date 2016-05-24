package it.polito.group2.restaurantowner.gallery;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.polito.group2.restaurantowner.R;

public class FullScreenGalleryAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<String> mGridData;

    // constructor
    public FullScreenGalleryAdapter(Activity activity, ArrayList<String> mGridData) {
        this.activity = activity;
        this.mGridData = mGridData;
    }

    @Override
    public int getCount() {
        return this.mGridData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.full_screen_gallery_item, container, false);

        PinchToZoomImageView imgDisplay = (PinchToZoomImageView) viewLayout.findViewById(R.id.gallery_item_image);
        final ProgressBar mProgressBar = (ProgressBar) viewLayout.findViewById(R.id.progress_bar);

        String imagesURL = mGridData.get(position);
        mProgressBar.setVisibility(View.VISIBLE);

        Glide
                .with(activity.getApplicationContext())
                .load(imagesURL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.blank_profile)
                .into(imgDisplay);

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);

    }

}