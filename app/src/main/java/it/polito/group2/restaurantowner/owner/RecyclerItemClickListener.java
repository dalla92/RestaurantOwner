package it.polito.group2.restaurantowner.owner;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Daniele on 13/04/2016.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    Rect outRect = new Rect();
    int[] location = new int[2];

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        //allow to click the meal availability switch
        if(childView instanceof CardView) {
            Switch sw = (Switch) childView.findViewById(R.id.meal_availability);
            if(sw!=null) {
                int[] values = new int[2];
                int w = sw.getWidth();
                int h = sw.getHeight();
                childView.getLocationOnScreen(values);
                if(inViewInBounds(sw,(int)e.getRawX(),(int)e.getRawY())){
                    return false;
                }
            }
        }
        //allow to click meal photo
        if(childView instanceof CardView) {
            /*
            LinearLayout ll1 = (LinearLayout) childView.getParent();
            LinearLayout ll2 = (LinearLayout) ll1.getParent();
            */
            ImageView mp = (ImageView) view.findViewById(R.id.meal_image);
            if(mp!=null) {
                int[] values = new int[2];
                int w = mp.getWidth();
                int h = mp.getHeight();
                childView.getLocationOnScreen(values);
                Log.d("bbb", "PASS");
                if(inViewInBounds(mp,(int)e.getRawX(),(int)e.getRawY())){
                    return false;
                }
            }
        }
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
            return true;
        }
        return false;
    }


    private boolean inViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}