package it.polito.group2.restaurantowner;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alessio on 09/04/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CommentViewHolder> {

    List<Comment> comments;
    Context context;

    RVAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    //public static class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView CommentUsername;
        TextView CommentDate;
        ImageView CommentPhoto;
        RatingBar CommentStars;
        TextView Comment;

        //TextView courseCRN;


        CommentViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            CommentUsername = (TextView) itemView.findViewById(R.id.comment_username);
            CommentDate = (TextView) itemView.findViewById(R.id.comment_date);
            CommentPhoto = (ImageView) itemView.findViewById(R.id.comment_photo);
            CommentStars = (RatingBar) itemView.findViewById(R.id.comment_stars);
            Comment = (TextView) itemView.findViewById(R.id.comment);
        }

        /*
        @Override
        public boolean onLongClick(View v) {
            Log.d("something", "happened");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
            Comment.setLayoutParams(lp);
            return false;
        }

        @Override
        public void onClick(View v) {
            Log.d("something", "happened");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
            Comment.setLayoutParams(lp);
        }
        */
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_card_view, viewGroup, false);
        CommentViewHolder pvh = new CommentViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder CommentViewHolder, int i) {
        CommentViewHolder.CommentUsername.setText(comments.get(i).username);
        CommentViewHolder.CommentDate.setText(comments.get(i).date);
        CommentViewHolder.CommentStars.setRating((float) comments.get(i).stars_number);
        CommentViewHolder.CommentPhoto.setImageResource(comments.get(i).photoId);

        //CommentViewHolder.Comment.setText(comments.get(i).comment);
        String comment_ell = comments.get(i).comment.substring(0, 7);
        //comment_ell.concat(this.context.getResources().getString(R.string.show_more));
        comment_ell = comment_ell + this.context.getResources().getString(R.string.show_more);
        CommentViewHolder.Comment.setText(comment_ell);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
