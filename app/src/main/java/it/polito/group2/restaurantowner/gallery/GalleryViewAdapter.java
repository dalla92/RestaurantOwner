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
import com.bumptech.glide.Glide;

import it.polito.group2.restaurantowner.R;

public class GalleryViewAdapter extends ArrayAdapter<GalleryViewItem> {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GalleryViewItem> mGridData = new ArrayList<>();

    public GalleryViewAdapter(Context mContext, int layoutResourceId, ArrayList<GalleryViewItem> mGridData) {
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
    public void setGridData(ArrayList<GalleryViewItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        GalleryViewItem item = mGridData.get(position);

        Glide.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}