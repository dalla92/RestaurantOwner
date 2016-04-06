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

        Intent intent = getIntent();
        int imageId1 = intent.getIntExtra(Enlarged_image.class.getName(), 0);
        if(imageId1!=0) {
            //Uri uri=Uri.parse("R.drawable.image");
            Uri image_uri = Uri.parse(String.valueOf(imageId1));
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
                myimage.setScaleType(ImageView.ScaleType.MATRIX);
            }
        }
        else
            Log.e("ENLARGED_IMAGE", "There is no image to enlarge");


        setContentView(R.layout.enlarged_image);

    }
}
