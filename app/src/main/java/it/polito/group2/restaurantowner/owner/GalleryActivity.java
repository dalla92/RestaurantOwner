package it.polito.group2.restaurantowner.owner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

public class GalleryActivity extends AppCompatActivity {

    private int restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            restaurantID = b.getInt("restaurantID");
        } else {
            //error on restaurant id
        }
    }

    public ArrayList<Photo> readJsonCommentList()
            throws JSONException {
        String json = null;
        ArrayList<Photo> photoList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "photoList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return photoList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Photos");
        Photo photo;

        //Iterate the jsonArray and print the info of JSONObjects
        for(int i=0; i < jsonArray.length(); i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(Integer.getInteger(jsonObject.optString("RestaurantID")).equals(restaurantID)) {
                photo = new Photo(jsonObject.optString("Image"));
                photo.setName(jsonObject.optString("Name"));
                photo.setDescription(jsonObject.optString("Description"));
                photo.setUploadTime(jsonObject.optString("Date") + " " + jsonObject.optString("Time"));
                photoList.add(photo);
            }
        }
        return photoList;
    }
}
