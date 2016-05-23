package it.polito.group2.restaurantowner.gallery;

/**
 * Created by TheChuck on 09/05/2016.
 */
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.polito.group2.restaurantowner.R;

public class GalleryViewAdapter extends ArrayAdapter<String> {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<String> mGridData = new ArrayList<>();

    public GalleryViewAdapter(Context mContext, int layoutResourceId, ArrayList<String> mGridData) {
        super(mContext, layoutResourceId, mGridData);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     *
     *
     */
    public void setGridData(ArrayList<String> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        String imagesURL = mGridData.get(position);

        holder.progressBar.setVisibility(View.VISIBLE);
        Glide
                .with(mContext)
                .load(imagesURL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.blank_profile)
                .into(holder.imageView);

        return row;
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}