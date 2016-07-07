package it.polito.group2.restaurantowner.owner;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;

public class Enlarged_image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enlarged_image);
        String photouri = getIntent().getExtras().getString("photouri");
        Log.d("ccc", "Photo uri is " + photouri);
        Uri image_uri = Uri.parse(photouri);
        ImageView myimage = (ImageView) findViewById(R.id.enlarged_image);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion <= Build.VERSION_CODES.KITKAT){
            myimage.setImageURI(image_uri);
        } else {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            } catch (Exception e) {
                Log.e("Exception", "Exception occurred in MediaStore.Images.Media.getBitmap");
            }
            if (bitmap != null) {
                myimage.setImageBitmap(bitmap);
                //myimage.setScaleType(ImageView.ScaleType.MATRIX);
            }
        }
    }

}
