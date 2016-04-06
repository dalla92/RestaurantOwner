package it.polito.group2.restaurantowner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class Enlarged_image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarged_image);

        Intent intent = getIntent();
        int imageId1 = intent.getIntExtra(Enlarged_image.class.getName(), 0);
        if(imageId1!=0) {
            InputStream is = this.getResources().openRawResource(imageId1);
            Bitmap originalBitmap = BitmapFactory.decodeStream(is);
            ImageView myimage = (ImageView) findViewById(R.id.enlarged_image);
            myimage.setImageBitmap(originalBitmap);
            myimage.setScaleType(ImageView.ScaleType.MATRIX);
        }
        else
            Log.e("ENLARGED_IMAGE", "There is no image to enlarge");
    }
}
