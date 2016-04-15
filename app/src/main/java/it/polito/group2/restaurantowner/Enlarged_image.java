package it.polito.group2.restaurantowner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class Enlarged_image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enlarged_image);
        String photouri = getIntent().getExtras().getString("photouri");
        Log.d("ccc", "Photo uri is " + photouri);
        Uri image_uri = Uri.
                parse(photouri);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
        }
        catch(Exception e){
            Log.e("Exception" , "Exception occurred in MediaStore.Images.Media.getBitmap");
        }
        ImageView myimage = (ImageView) findViewById(R.id.enlarged_image);
        if(bitmap != null) {
            myimage.setImageBitmap(bitmap);
            //myimage.setScaleType(ImageView.ScaleType.MATRIX);
        }
    }
}
