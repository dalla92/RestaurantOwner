package it.polito.group2.restaurantowner.user.restaurant_page.gallery;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import it.polito.group2.restaurantowner.R;

public class FullScreenGalleryAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<GalleryViewItem> mGridData;

    // constructor
    public FullScreenGalleryAdapter(Activity activity, ArrayList<GalleryViewItem> mGridData) {
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
        PinchToZoomImageView imgDisplay;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.full_screen_gallery_item, container, false);

        imgDisplay = (PinchToZoomImageView) viewLayout.findViewById(R.id.gallery_item_image);

        Glide.with(activity.getApplicationContext()).load(mGridData.get(position).getImage()).into(imgDisplay);

        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mGridData.get(position), options);
        imgDisplay.setImageBitmap(bitmap);*/

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);

    }

}