package it.polito.group2.restaurantowner.gallery;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.RestaurantGallery;
import it.polito.group2.restaurantowner.firebasedata.User;

/**
 * Created by TheChuck on 09/05/2016.
 */

public class GalleryViewActivity extends AppCompatActivity {

    private GalleryViewAdapter mGridAdapter;
    private ArrayList<String> mGridData;
    private String restaurantID;
    private GridView mGridView;
    public int PICK_IMAGE = 0;
    public int REQUEST_TAKE_PHOTO = 1;
    private StorageReference storageRef;
    private FirebaseDatabase firebase;
    private String mCurrentPhotoPath;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        restaurantID = "-KIMqPtRSEdm0Cvfc3Za";
        //restaurantID = bundle.getString("restaurantID");

        firebase = FirebaseDatabase.getInstance();

        mGridView = (GridView) findViewById(R.id.gridView);

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://have-break-9713d.appspot.com");
        StorageReference image1Ref = storageRef.child("restaurants/-KIMqPtRSEdm0Cvfc3Za/img.jpg");
        StorageReference image2Ref = storageRef.child("restaurants/-KIMqPtRSEdm0Cvfc3Za/image9.png");
        final StorageReference listRef = storageRef.child("restaurants/-KIMqPtRSEdm0Cvfc3Za/list.txt");

        showProgressDialog();
        DatabaseReference galleryRef =  firebase.getReference("restaurants_galleries/" + restaurantID);
        galleryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RestaurantGallery gallery = dataSnapshot.getValue(RestaurantGallery.class);
                mGridData = new ArrayList<>();
                mGridData.addAll(gallery.getUrls().values());
                mGridAdapter = new GalleryViewAdapter(GalleryViewActivity.this, R.layout.gallery_item, mGridData);
                mGridView.setAdapter(mGridAdapter);
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("prova", "cancelled");
                hideProgressDialog();
            }
        });

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(GalleryViewActivity.this, FullScreenGalleryActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra("position", position)
                        .putExtra("urls", mGridData);

                Bundle b = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
                    //bitmap.eraseColor(colour);
                    b = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(imageView, bitmap, 0, 0).toBundle();
                }

                //Start details activity
                startActivity(intent, b);
                //overridePendingTransition(0,0);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_photo) {
            View menuItemView = findViewById(R.id.action_add_photo);
            PopupMenu popup = new PopupMenu(GalleryViewActivity.this, menuItemView);
            popup.getMenuInflater().inflate(R.menu.menu_popup_pictures, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.camera) {
                        dispatchTakePictureIntent();
                        return true;
                    }
                    if (id == R.id.gallery) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        return true;
                    } else {
                        return true;
                    }
                }
            });
            popup.setGravity(Gravity.CENTER);
            popup.show();
        }

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("aaa", "BREAK1");
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //import util or sql?
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take a photo result
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            showProgressDialog();
            galleryAddPic();
            File f = new File(mCurrentPhotoPath);
            Uri imageUri = Uri.fromFile(f);

            final Calendar c = Calendar.getInstance();
            StorageReference imageRef = storageRef.child("restaurants/" + restaurantID + "/" +
                    c.get(Calendar.DAY_OF_MONTH) + "_" +
                    c.get(Calendar.MONTH) + "_" +
                    c.get(Calendar.YEAR) + ".jpg");

            //uploading image on the firebase storage
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GalleryViewActivity.this, "Error during save of the picture, try again!", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference galleryRef =  firebase.getReference("restaurants_galleries/" + restaurantID + "/urls");
                    galleryRef.push().setValue(downloadUrl.toString());
                    mGridData.add(downloadUrl.toString());
                    mGridAdapter.setGridData(mGridData);
                    hideProgressDialog();
                }
            });
        }

        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            showProgressDialog();
            Uri imageUri = data.getData();

            final Calendar c = Calendar.getInstance();
            StorageReference imageRef = storageRef.child("restaurants/" + restaurantID + "/" +
                    c.get(Calendar.DAY_OF_MONTH) + "_" +
                    c.get(Calendar.MONTH) + "_" +
                    c.get(Calendar.YEAR) + ".jpg");

            //uploading image on the firebase storage
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GalleryViewActivity.this, "Error during save of the picture, try again!", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference galleryRef =  firebase.getReference("restaurants_galleries/" + restaurantID + "/urls");
                    galleryRef.push().setValue(downloadUrl.toString());
                    mGridData.add(downloadUrl.toString());
                    mGridAdapter.setGridData(mGridData);
                    hideProgressDialog();
                }
            });

        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}

