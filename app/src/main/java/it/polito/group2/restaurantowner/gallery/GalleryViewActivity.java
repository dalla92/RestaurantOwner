package it.polito.group2.restaurantowner.gallery;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.ImageUtils;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.PermissionUtil;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantGallery;

/**
 * Created by TheChuck on 09/05/2016.
 */

public class GalleryViewActivity extends AppCompatActivity {

    private GalleryViewAdapter mGridAdapter;
    private HashMap<String, String> mGridData;
    private String restaurantID;
    private GridView mGridView;
    private final int PICK_IMAGE = 0;
    private final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private StorageReference storageRef;
    private FirebaseDatabase firebase;
    private String mCurrentPhotoPath;
    private ProgressDialog mProgressDialog;
    private Boolean isOwner = false;
    private String userID;
    private boolean coming_from_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebase = FirebaseDatabase.getInstance();
        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        mGridView = (GridView) findViewById(R.id.gridView);

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id") != null)
            restaurantID = getIntent().getExtras().getString("restaurant_id");

        if(getIntent().getExtras()!=null)
            coming_from_user = getIntent().getExtras().getBoolean("coming_from_user");

        FirebaseUtil.showProgressDialog(mProgressDialog);

        userID = FirebaseUtil.getCurrentUserId();
        if(userID != null){
            DatabaseReference resRef = firebase.getReference("restaurants/" + restaurantID);
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Restaurant res = dataSnapshot.getValue(Restaurant.class);
                    if(res != null && res.getUser_id().equals(userID))
                        isOwner = true;
                    else
                        isOwner = false;

                    if(isOwner){
                        PermissionUtil.checkWritePermission(GalleryViewActivity.this, REQUEST_WRITE_STORAGE);
                        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
                        mGridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                            @Override
                            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                                final int checkedCount = mGridView.getCheckedItemCount();
                                // Set the CAB title according to total checked items
                                mode.setTitle(checkedCount + " Selected");
                                // Calls toggleSelection method from ListViewAdapter Class
                                mGridAdapter.toggleSelection(position);
                            }

                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                getSupportActionBar().hide();
                                mode.getMenuInflater().inflate(R.menu.menu_gallery_selected, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_delete:
                                        AlertDialog.Builder alert = new AlertDialog.Builder(GalleryViewActivity.this);
                                        alert.setTitle(getResources().getString(R.string.action_confirm));
                                        alert.setMessage(getResources().getString(R.string.delete_image));
                                                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                // Calls getSelectedIds method from ListViewAdapter Class
                                                dialog.dismiss();
                                                SparseBooleanArray selected = mGridAdapter.getSelectedIds();
                                                // Captures all selected ids with a loop
                                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                                    showProgressDialog();
                                                    if (selected.valueAt(i)) {
                                                        final String selectedItemKey = (String) mGridAdapter.getItem(selected.keyAt(i));
                                                        String url = mGridData.get(selectedItemKey);

                                                        //remove item from storage, database and adapter
                                                        int index = url.indexOf("?");
                                                        final String fileName = url.substring(index - 17, index);

                                                        StorageReference imageRef = storageRef.child("restaurants/" + restaurantID + "/" + fileName);
                                                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                DatabaseReference galleryRef = firebase.getReference("restaurants_galleries/" + restaurantID + "/urls");
                                                                galleryRef.child(selectedItemKey).removeValue(new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                        mGridAdapter.remove(selectedItemKey);
                                                                        hideProgressDialog();
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(GalleryViewActivity.this, getResources().getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                }
                                                // Close CAB
                                                mode.finish();
                                            }
                                        });
                                        alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mode.finish();
                                                dialog.dismiss();
                                            }
                                        });
                                        alert.show();
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                mGridAdapter.removeSelection();
                                getSupportActionBar().show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            /*userRef = firebase.getReference("users/" + userID + "/ownerUser");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    isOwner = dataSnapshot.getValue(Boolean.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
        }

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://have-break-9713d.appspot.com");


        DatabaseReference galleryRef =  firebase.getReference("restaurants_galleries/" + restaurantID);
        galleryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RestaurantGallery gallery = dataSnapshot.getValue(RestaurantGallery.class);
                if(gallery!=null) {
                    mGridData = new HashMap<>(gallery.getUrls());
                    //mGridData.addAll(gallery.getUrls().values());
                    mGridAdapter = new GalleryViewAdapter(GalleryViewActivity.this, R.layout.gallery_item, mGridData);
                    mGridView.setAdapter(mGridAdapter);
                }
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

                ArrayList<String> urls = new ArrayList<>();
                urls.addAll(mGridData.values());

                for(String url: urls){
                    Log.d("gallery", url);
                }

                //Pass the image title and url to DetailsActivity
                intent.putExtra("position", position)
                        .putExtra("urls", urls);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(GalleryViewActivity.this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        userID = FirebaseUtil.getCurrentUserId();
        if(userID == null)
            getMenuInflater().inflate(R.menu.menu_gallery_user, menu);
        else {
            DatabaseReference resRef = firebase.getReference("restaurants/" + restaurantID);
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Restaurant res = dataSnapshot.getValue(Restaurant.class);
                    if(res != null && res.getUser_id().equals(userID))
                        isOwner = true;
                    else
                        isOwner = false;

                    if (isOwner)
                        getMenuInflater().inflate(R.menu.menu_gallery_owner, menu);
                    else
                        getMenuInflater().inflate(R.menu.menu_gallery_user, menu);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        /*if(userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren())
                        isOwner = data.getValue(Boolean.class);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    getMenuInflater().inflate(R.menu.menu_gallery_user, menu);
                }
            });
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(isOwner) {
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
        }

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(isOwner) {
            if (coming_from_user) {
                OnBackUtil.clean_stack_and_go_to_user_restaurant_page(this, restaurantID);
            }
            else{
                OnBackUtil.clean_stack_and_go_to_restaurant_page(this, restaurantID);
            }
        }
        else
            OnBackUtil.clean_stack_and_go_to_user_restaurant_page(this, restaurantID);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
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

            /*File f = new File(mCurrentPhotoPath);
            Uri imageUri = Uri.fromFile(f);*/

            ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                @Override
                protected void onPostExecute(Uri imageUri) {
                    // image here is compressed & ready to be saved
                    galleryAddPic();
                    Calendar c = Calendar.getInstance();
                    StorageReference imageRef = storageRef.child("restaurants/" + restaurantID + "/" +
                            c.getTimeInMillis() + ".jpg");

                    //uploading image on the firebase storage
                    UploadTask uploadTask = imageRef.putFile(imageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GalleryViewActivity.this, getResources().getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            DatabaseReference galleryRef = firebase.getReference("restaurants_galleries/" + restaurantID + "/urls");
                            DatabaseReference itemRef = galleryRef.push();
                            itemRef.setValue(downloadUrl.toString());
                            mGridData.put(itemRef.getKey(), downloadUrl.toString());
                            mGridAdapter.setGridData(mGridData);
                            hideProgressDialog();
                        }
                    });
                }
            };
            imageCompression.execute(mCurrentPhotoPath);// imagePath as a string


        }

        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            showProgressDialog();
            Uri imageUri = data.getData();

            final Calendar c = Calendar.getInstance();
            StorageReference imageRef = storageRef.child("restaurants/" + restaurantID + "/" +
                    c.getTimeInMillis() + ".jpg");

            //uploading image on the firebase storage
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GalleryViewActivity.this, getResources().getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference galleryRef =  firebase.getReference("restaurants_galleries/" + restaurantID + "/urls");
                    DatabaseReference itemRef = galleryRef.push();
                    itemRef.setValue(downloadUrl.toString());
                    mGridData.put(itemRef.getKey(), downloadUrl.toString());
                    mGridAdapter.setGridData(mGridData);
                    hideProgressDialog();
                }
            });

        }
    }


    public abstract class ImageCompressionAsyncTask extends AsyncTask<String, Void, Uri> {

        @Override
        protected Uri doInBackground(String... strings) {
            if(strings.length == 0 || strings[0] == null)
                return null;
            return ImageUtils.compressImage(strings[0]);
        }

        protected abstract void onPostExecute(Uri imageUri) ;
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

