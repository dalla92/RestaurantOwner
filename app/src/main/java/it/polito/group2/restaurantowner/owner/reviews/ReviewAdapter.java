package it.polito.group2.restaurantowner.owner.reviews;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.OnLoadMoreListener;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.owner.ItemTouchHelperAdapter;

public class ReviewAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final FirebaseDatabase firebase;

    private ArrayList<Review> reviews;
    private Context context;
    private RecyclerView recyclerView;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public ReviewAdapter(ArrayList<Review> reviews, Context context, RecyclerView recyclerView) {
        this.reviews = reviews;
        this.context = context;
        this.recyclerView = recyclerView;
        firebase = FirebaseDatabase.getInstance();
    }

    public void updateScrollListener(boolean moreReviews){
        if(moreReviews){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            addNullItem();
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
        else{
            recyclerView.clearOnScrollListeners();
        }
    }


    public void addNullItem() {
        reviews.add(null);
        notifyItemInserted(reviews.size() - 1);
    }

    public void removeNullItem() {
        int indexOfItem = reviews.indexOf(null);
        if (indexOfItem != -1) {
            this.reviews.remove(indexOfItem);
            notifyItemRemoved(indexOfItem);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return reviews.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review, parent, false);
            vh = new ReviewViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReviewViewHolder) {

            ReviewViewHolder reviewHolder = (ReviewViewHolder) holder;
            reviewHolder.username.setText(reviews.get(position).getUser_full_name());
            reviewHolder.stars.setRating(reviews.get(position).getReview_rating());

            SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(reviews.get(position).getReview_timestamp());
            reviewHolder.date.setText(format.format(c.getTime()));

            String thumbnail = reviews.get(position).getUser_thumbnail();

            if(thumbnail != null && !thumbnail.equals(""))
                Glide.with(context).load(thumbnail).into(reviewHolder.picture);
            else
                Glide.with(context).load(R.drawable.blank_profile_thumb).into(reviewHolder.picture);

            if (reviews.get(position).getReview_comment().equals(""))
                reviewHolder.comment.setVisibility(View.GONE);
            else
                reviewHolder.comment.setText(reviews.get(position).getReview_comment());
        }
        else {
            ((ProgressViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView username, date, comment;
        public ImageView picture;
        public RatingBar stars;

        public ReviewViewHolder(View view){
            super(view);
            username = (TextView) itemView.findViewById(R.id.user_review_username);
            date = (TextView) itemView.findViewById(R.id.user_review_date);
            picture = (ImageView) itemView.findViewById(R.id.user_review_picture);
            stars = (RatingBar) itemView.findViewById(R.id.user_review_stars);
            comment = (TextView) itemView.findViewById(R.id.user_review_comment);
            comment.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v instanceof TextView) {
                TextView tv = (TextView) v;
                int collapsedMaxLines = 2;
                ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getMaxLines() == collapsedMaxLines? 200 : collapsedMaxLines);
                animation.setDuration(200).start();
            }
        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

}
