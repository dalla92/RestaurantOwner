package it.polito.group2.restaurantowner.gallery;

/**
 * Created by TheChuck on 09/05/2016.
 */
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.polito.group2.restaurantowner.R;

public class GalleryViewAdapter extends BaseAdapter {

    private Context mContext;
    private int layoutResourceId;
    private String[] mKeys;
    private HashMap<String, String> mGridData;
    private SparseBooleanArray mSelectedItemsIds;

    public GalleryViewAdapter(Context mContext, int layoutResourceId, HashMap<String, String> mGridData) {
        //super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
        mKeys = mGridData.keySet().toArray(new String[mGridData.size()]);
        mSelectedItemsIds = new SparseBooleanArray();
    }


    /**
     * Updates grid data and refresh grid items.
     */
    public void setGridData(HashMap<String, String> mGridData) {
        this.mGridData = mGridData;
        mKeys = mGridData.keySet().toArray(new String[mGridData.size()]);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mGridData.size();
    }

    @Override
    public Object getItem(int position) {
        return mKeys[position];
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
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

        String key = mKeys[position];
        String imagesURL = mGridData.get(key);
        //String imagesURL = mGridData.get(position);

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

    public void remove(String key) {
        mGridData.remove(key);
        mKeys = mGridData.keySet().toArray(new String[mGridData.size()]);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    /*public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }*/

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}