package it.polito.group2.restaurantowner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.github.mikephil.charting.data.LineData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private int restaurantID;

    private RecyclerView rv;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Comment> commentList;
    private boolean card_view_clicked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            restaurantID = b.getInt("restaurantID");
        } else {
            //error on restaurant id
        }

        try {
            commentList = readJsonCommentList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //cardview implementation
        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapterComments();
        final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        ScrollView scroll = (ScrollView) findViewById(R.id.parent_scroll);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                appbar.setExpanded(false);
            }
        });

        ImageButton replyBtn =(ImageButton)findViewById(R.id.replyBtn);
        ImageButton worningBtn =(ImageButton)findViewById(R.id.worningBtn);

        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyToReview();
            }
        });

        worningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worningReview();
            }
        });
    }

    public void replyToReview() {
        //i don't know how to pass the comment id
    }

    public void worningReview() {
        //i don't know how to pass the comment id
    }

    private void initializeAdapterComments(){
        Adapter_Comments adapter = new Adapter_Comments(commentList, this.getApplicationContext());
        rv.setAdapter(adapter);
    }

    public ArrayList<Comment> readJsonCommentList()
            throws JSONException {
        String json = null;
        ArrayList<Comment> commentList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "commentList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return commentList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Comments");
        Comment comment;

        //Iterate the jsonArray and print the info of JSONObjects
        for(int i=0; i < jsonArray.length(); i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(Integer.getInteger(jsonObject.optString("RestaurantID")).equals(restaurantID)) {
                comment = new Comment();
                comment.setDate(jsonObject.optString("Date") + " " + jsonObject.optString("Time"));
                commentList.add(comment);
            }
        }
        return commentList;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // TODO Handle the logout action
        } else if (id == R.id.nav_manage) {
            // TODO Handle the manage action
        } /*else if (id == R.id.one_restaurant) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    Restaurant_page.class);
            Bundle b = new Bundle();
            b.putInt("restaurant_id", restaurant_id);
            intent.putExtras(b);
            startActivity(intent);
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
