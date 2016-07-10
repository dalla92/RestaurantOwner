package it.polito.group2.restaurantowner.owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.user.restaurant_page.ReviewAdapter;

public class Restaurant_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Menu menu;
    private RecyclerView rv;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean card_view_clicked=false;
    public String restaurant_id; //if not passed by the previous activity
    public int PICK_IMAGE = 0;
    public int REQUEST_TAKE_PHOTO = 1;
    public int MODIFY_INFO = 4;
    public String photouri = null;
    public ArrayList<Review> reviews = new ArrayList<>();
    public Restaurant my_restaurant = null;
    public Context context;
    private DatabaseReference q;
    private Query q2;
    private ValueEventListener l;
    private ValueEventListener l2;
    private ProgressDialog mProgressDialog;
    private ReviewAdapter adapter;
    private StorageReference photo_reference;
    private StorageReference user_storage_reference;
    private FirebaseDatabase firebase;
    Toolbar toolbar;

    /*private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    private String mCurrentPhotoPath, bitmapPath;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);

        context = this;

        //get the right restaurant
        Bundle b = getIntent().getExtras();

        if(b!=null)
            restaurant_id = b.getString("RestaurantId");

        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        //get and fill related data
        get_data_from_firebase();

        //collapsing bar
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //prepare enlarged image option
        ImageView imageview = (ImageView) findViewById(R.id.image_to_enlarge);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(my_restaurant != null && my_restaurant.getRestaurant_photo_firebase_URL()!=null) {
                        Intent intent = new Intent(
                                getApplicationContext(),
                                Enlarged_image.class);
                        Bundle b = new Bundle();
                        b.putString("photouri", my_restaurant.getRestaurant_photo_firebase_URL());
                        intent.putExtras(b);
                        startActivity(intent);
                }
            }
        });
        setUpPicture();

        //cardview implementation
        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapterReviews();
        final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        NestedScrollView scroll = (NestedScrollView) findViewById(R.id.parent_scroll);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                appbar.setExpanded(false);
            }
        });

        //buttons listeners implementation
        //take a photo
        Button button1 = (Button) findViewById(R.id.button_take_photo);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        //choose a photo
        Button button2 = (Button) findViewById(R.id.button_choose_photo);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    private void get_data_from_firebase(){
        firebase = FirebaseDatabase.getInstance();
        setDrawer();
        q = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/" + restaurant_id);
        l = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                my_restaurant = snapshot.getValue(Restaurant.class);
                getSupportActionBar().setTitle(my_restaurant.getRestaurant_name());
                if(my_restaurant!=null)
                    fill_data();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };

        //reviews
        q2 = firebase.getReference("reviews/" + restaurant_id).orderByPriority();
        l2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clearReviews();
                for(DataSnapshot data: dataSnapshot.getChildren())
                    adapter.addReview(data.getValue(Review.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("prova", "child cancelled");
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUtil.initProgressDialog(this);
        q.addListenerForSingleValueEvent(l);
        q2.addValueEventListener(l2);
    }
    @Override
    protected void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_value_event_listener(q, l);
        RemoveListenerUtil.remove_value_event_listener(q2, l2);
    }

    private void fill_data(){
        ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
        if (my_restaurant.getRestaurant_photo_firebase_URL()!= null) {
            Glide.with(context)
                    .load(my_restaurant.getRestaurant_photo_firebase_URL())
                    .into(image);
        }
        EditText edit_restaurant_name = (EditText) findViewById(R.id.edit_restaurant_name);
        EditText edit_restaurant_address = (EditText) findViewById(R.id.edit_restaurant_address);
        EditText edit_restaurant_telephone_number = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
        if(my_restaurant !=null && my_restaurant.getRestaurant_name()!=null)
            edit_restaurant_name.setText(my_restaurant.getRestaurant_name());
        if(my_restaurant !=null && my_restaurant.getRestaurant_address()!=null)
            edit_restaurant_address.setText(my_restaurant.getRestaurant_address());
        if(my_restaurant !=null && my_restaurant.getRestaurant_telephone_number()!=null)
            edit_restaurant_telephone_number.setText(my_restaurant.getRestaurant_telephone_number());

        getSupportActionBar().setTitle(my_restaurant.getRestaurant_name());
        FirebaseUtil.hideProgressDialog(mProgressDialog);

    }

    private void setUpPicture() {
        final ImageView img = (ImageView) findViewById(R.id.edit_cover_picture);
        if (img != null) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(Restaurant_page.this, img);
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
                    /*if (popup.getDragToOpenListener() instanceof ListPopupWindow.ForwardingListener) {
                        ListPopupWindow.ForwardingListener listener = (ListPopupWindow.ForwardingListener) popup.getDragToOpenListener();
                        listener.getPopup().setVerticalOffset(-(img.getHeight()) / 2);
                        listener.getPopup().show();
                    }*/
                }
            });
        }
    }

    /*
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("photouri", photouri);
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString(restaurant_id, photouri);
        edit.commit();
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state!=null) {
            if(state.getString("photouri")!=null) {
                ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
                image.setImageURI(Uri.parse(state.getString("photouri")));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        get_data_from_firebase();
    }
    */


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            OnBackUtil.clean_stack_and_go_to_main_activity(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main_restaurant, menu);
        //this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
        return DrawerUtil.drawer_owner_restaurant_page(this, item, restaurant_id, my_restaurant);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take a photo result
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //view photo
            ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
            setPic();
            //add photo to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            if(photouri!=null) {
                File f = new File(photouri); //here is passed the mCurrentPhotoPath
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                //image.setImageURI(Uri.parse(photouri));
                Glide.with(context)
                        .load(photouri) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.blank_profile)
                        .into(image);
                //my_restaurant.setRestaurant_photo_firebase_URL(contentUri.toString());
                save_photo();
            }
        }

        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            //photouri = imageUri.toString();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                ImageView image_to_enlarge = (ImageView) findViewById(R.id.image_to_enlarge);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                Glide.with(context)
                        .load(getImageUri(context, bitmap)) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.blank_profile)
                        .into(image_to_enlarge);
                photouri = saveToInternalStorage(bitmap);
                save_photo();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        // Ignore the exception
                    }
                }
            }
        }

        if (requestCode == MODIFY_INFO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Restaurant res = (Restaurant) data.getExtras().get("Restaurant");
                my_restaurant = res;
                FirebaseDatabase firebase = FirebaseDatabase.getInstance();
                DatabaseReference resReference = firebase.getReference("restaurants/" + res.getRestaurant_id());
                resReference.setValue(res);
            }
        }

        hide();
    }

    private void setDrawer() {
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        String userID = FirebaseUtil.getCurrentUserId();
        if (userID != null) {

            DatabaseReference userRef = firebase.getReference("users/" + userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                    TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(it.polito.group2.restaurantowner.firebasedata.User.class);

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());

                    String photoUri = target.getUser_photo_firebase_URL();
                    if(photoUri == null || photoUri.equals("")) {
                        Glide
                                .with(getApplicationContext())
                                .load(R.drawable.blank_profile_nav)
                                .centerCrop()
                                .into(nav_picture);
                    }
                    else{
                        Glide
                                .with(getApplicationContext())
                                .load(photoUri)
                                .centerCrop()
                                .into(nav_picture);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("prova", "cancelled");
                }
            });

        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (photo_reference != null) {
            outState.putString("reference", photo_reference.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        photo_reference = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = photo_reference.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //
                }
            });
        }
    }

    public void save_photo(){
        //save photo
        ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        user_storage_reference = storage.getReferenceFromUrl("gs://have-break-9713d.appspot.com");
        File f = new File(photouri);
        Uri imageUri = Uri.fromFile(f);
        // Create a child reference
        // imagesRef now points to "images"
        Calendar c = Calendar.getInstance();
        photo_reference = user_storage_reference.child("restaurants/" + restaurant_id + "/" + c.getTimeInMillis() + ".jpg");
        //upload
        UploadTask uploadTask = photo_reference.putFile(imageUri);
        uploadTask
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    //public void onFailure(@NonNull Throwable throwable) {
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Log.d("my_ex", e.getMessage());
                        Toast failure_message = Toast.makeText(context, "The upload is failed", Toast.LENGTH_LONG);
                        failure_message.show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        my_restaurant.setRestaurant_photo_firebase_URL(downloadUrl.toString());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/" + restaurant_id + "/restaurant_photo_firebase_URL");
                        ref.setValue(downloadUrl.toString());

                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants_previews/" + restaurant_id + "/restaurant_cover_firebase_URL");
                        ref2.setValue(downloadUrl.toString());
                    }
                });

    }

    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath= new File(directory, "profile.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return myPath.getAbsolutePath();
    }

    private void initializeAdapterReviews(){
        adapter = new ReviewAdapter(reviews, this.getApplicationContext());
        rv.setAdapter(adapter);
    }

    public void myClickHandler_expand_comment(View v){
        final View default_view = v;
        TextView comment = (TextView) v.findViewById(R.id.comment);
        final int original_comment_height=comment.getMeasuredHeight();
        int i;
        String comment_start = comment.getText().toString().substring(0, 7);
        for(i=0; i<reviews.size(); i++){
            if(comment_start.equals(reviews.get(i).getReview_comment().substring(0, 7)))
                break;
        }
        if(!card_view_clicked) {
            card_view_clicked=true;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
            comment.setMaxLines(Integer.MAX_VALUE);
            comment.setText(reviews.get(i).getReview_comment());
            comment.setLayoutParams(lp);
        }
        else {
            card_view_clicked=false;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, v.getLayoutParams().height, 1f);
            String comment_ell = reviews.get(i).getReview_comment().substring(0, 7);
            comment_ell = comment_ell + getResources().getString(R.string.show_more);
            comment.setText(comment_ell);
            comment.setMaxLines(2);
            comment.setLayoutParams(lp);
        }
        v.requestLayout();
    }

    public void show(){
        //button present yes/not
        Button button_take_photo1 = (Button) findViewById(R.id.button_take_photo);
        button_take_photo1.setVisibility(View.VISIBLE);
        button_take_photo1.requestFocus();
        Button button_choose_photo1 = (Button) findViewById(R.id.button_choose_photo);
        button_choose_photo1.setVisibility(View.VISIBLE);
        button_choose_photo1.requestFocus();
    }

    public void hide(){
        //button present yes/not
        Button button_take_photo2 = (Button) findViewById(R.id.button_take_photo);
        button_take_photo2.setVisibility(View.GONE);
        Button button_choose_photo2 = (Button) findViewById(R.id.button_choose_photo);
        button_choose_photo2.setVisibility(View.GONE);
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
        photouri = image.getAbsolutePath();
        return image;
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

    private void setPic() {
        ImageView mImageView = (ImageView) findViewById(R.id.image_to_enlarge);
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photouri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = 1;
        if(targetW!=0 && targetH!=0) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photouri, bmOptions);
        if(bitmap!=null)
            Glide.with(context)
                    .load(getImageUri(context, bitmap)) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.blank_profile)
                    .into(mImageView);
        /*
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString("photouri", photouri);
        //I can not save the photo, but i could save its URI
        edit.commit();
        */
    }


}
